import Shared
import SwiftUI

@MainActor
class LoginDialogViewModel : ObservableObject {
    
    private let authRepository: AuthRepository
    @Published var user: UserModel? = nil
    
    private var loginTask : Task<(), any Error>? = nil
    private var dataTask : Task<(), any Error>? = nil
    
    init() {
        authRepository = KoinHelper.shared.authRepository()
        
        dataTask = Task { [weak self] in
            guard let stream = self?.authRepository.getAuthedUserAsyncSequence() else { return }

            for try await user in stream {
                self?.user = user
            }
        }
    }

    deinit {
        dataTask?.cancel()
        loginTask?.cancel()
    }
}


struct LoginDialogView: View {
    @StateObject
    private var viewModel: LoginDialogViewModel = LoginDialogViewModel()
    
    var onLogout: (() -> Void)? = nil
    var onLogin: (() -> Void)? = nil
    var onSettingClick : (() -> Void)? = nil
    var onNotificationClick : (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            header
            
            VStack(spacing: 0) {
                if viewModel.user != nil {
                    actionRow(title: "Notification", systemImage: "bell") {
                        onNotificationClick?()
                    }
                    Divider()
                }
                
                actionRow(title: "Settings", systemImage: "gearshape") {
                    onSettingClick?()
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(.thinMaterial)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .overlay(
                RoundedRectangle(cornerRadius: 12, style: .continuous)
                    .strokeBorder(Color.white.opacity(0.08))
            )
            
            if viewModel.user != nil {
                Button("Logout", role: .destructive, action: {
                    onLogout?()
                })
                .buttonStyle(.borderedProminent)
                .tint(.red)
                .frame(maxWidth: .infinity)
            } else {
                Button("Login with AniList", action: {
                    onLogin?()
                })
                .buttonStyle(.borderedProminent)
                .tint(.accentColor)
                .frame(maxWidth: .infinity)
            }
        }
        .padding(16)
        .frame(maxWidth: 380, alignment: .leading)
        .padding(.horizontal, 16)
    }

    @ViewBuilder
    private var header: some View {
        HStack(spacing: 12) {
            Button(action: {}) {
                avatarView
            }
            .buttonStyle(.plain)
            
            VStack(alignment: .leading, spacing: 2) {
                Text(viewModel.user?.name ?? "Guest")
                    .font(.headline)
                    .lineLimit(1)
                    .truncationMode(.tail)
                
                Text(viewModel.user != nil ? "Signed in with AniList" : "Sign in to sync your list")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .lineLimit(2)
            }
            
            Spacer()
        }
        .frame(minHeight: 52)
    }

    private func actionRow(title: String, systemImage: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 10) {
                Image(systemName: systemImage)
                    .foregroundColor(.accentColor)
                    .frame(width: 18, height: 18)
                
                Text(title)
                    .foregroundColor(.primary)
                Spacer()
                Image(systemName: "chevron.right")
                    .foregroundColor(.secondary)
                    .font(.footnote.weight(.semibold))
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    private var avatarView: some View {
        if let urlStr = viewModel.user?.avatar, let url = URL(string: urlStr) {
            CustomAsyncImage(url: url.absoluteString, contentMode: .fill)
                .frame(width: 48, height: 48)
                .clipShape(Circle())
                .overlay(Circle().stroke(Color.accentColor.opacity(0.6), lineWidth: 2))
                .shadow(color: .black.opacity(0.08), radius: 4, x: 0, y: 2)
        } else {
            Image(systemName: "person.crop.circle")
                .resizable()
                .scaledToFit()
                .frame(width: 48, height: 48)
                .foregroundColor(.secondary)
                .overlay(Circle().stroke(Color.secondary, lineWidth: 1))
        }
    }
}
