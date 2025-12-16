import SDWebImageSwiftUI
import Shared
import SwiftUI

@MainActor
class LoginDialogViewModel : ObservableObject {
    
    private let authRepository: AuthRepository
    @Published var user: UserModel? = nil
    
    private var loginTask : Task<(), any Error>? = nil
    private var dataTask : Task<(), any Error>? = nil
    
    init() {
        print("LoginDialogViewModel init")
        
        authRepository = KoinHelper.shared.authRepository()
        
        dataTask = Task { [weak self] in
            guard let stream = self?.authRepository.getAuthedUserAsyncSequence() else { return }

            for try await user in stream {
                self?.user = user
            }
        }
    }

    deinit {
        print("LoginDialogViewModel deinit")
        dataTask?.cancel()
        loginTask?.cancel()
    }
}


struct LoginDialogView: View {
    @StateObject
    private var viewModel: LoginDialogViewModel = LoginDialogViewModel()
    @State private var avatarLoadFailed = false

    var onLogout: (() -> Void)? = nil
    var onLogin: (() -> Void)? = nil
    var onSettingClick : (() -> Void)? = nil
    var onNotificationClick : (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 12) {
                Button(action: {}) {
                    avatarView
                }
                .buttonStyle(PlainButtonStyle())
                
                Text(viewModel.user?.name ?? "")
                    .font(.title3).fontWeight(.semibold)
                    .lineLimit(1)
                    .truncationMode(.tail)
                
                Spacer()
            }
            .frame(minHeight: 44)
            .padding(.bottom, 8)
            
            Divider()
            
            VStack(spacing: 0) {
                if viewModel.user != nil {
                    Button(action: {
                        onNotificationClick?()
                    }) {
                        HStack {
                            Image(systemName: "bell")
                                .foregroundColor(.accentColor)
                            Text("Notification")
                            Spacer()
                        }
                        .padding(.vertical, 12)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
                Button(action: {
                    onSettingClick?()
                }) {
                    HStack {
                        Image(systemName: "gearshape")
                            .foregroundColor(.accentColor)
                        Text("Settings")
                        Spacer()
                    }
                    .padding(.vertical, 12)
                }
                .buttonStyle(PlainButtonStyle())
            }
            Divider()
            if viewModel.user != nil {
                Button("Logout", role: .destructive, action: {
                    onLogout?()
                })
                .buttonStyle(.borderedProminent)
                .tint(.red)
                .frame(maxWidth: .infinity)
                .padding(.top, 12)
            } else {
                Button("Login with AniList", action: {
                    onLogin?()
                })
                .buttonStyle(.borderedProminent)
                .tint(.blue)
                .frame(maxWidth: .infinity)
                .padding(.top, 12)
            }
        }
        .padding(8)
        .frame(maxWidth: 340)
        .padding(.horizontal, 12)
    }

    @ViewBuilder
    private var avatarView: some View {
        if let urlStr = viewModel.user?.avatar,
           let url = URL(string: urlStr),
           !avatarLoadFailed {
            WebImage(url: url) { image in
                image.resizable().aspectRatio(contentMode: .fill)
            } placeholder: {
                ProgressView()
            }
            .onFailure { _ in
                avatarLoadFailed = true
            }
            .frame(width: 44, height: 44)
            .clipShape(Circle())
            .overlay(Circle().stroke(Color.accentColor, lineWidth: 2))
            .shadow(radius: 2)
        } else {
            Image(systemName: "person.crop.circle")
                .resizable().scaledToFit()
                .frame(width: 44, height: 44)
                .overlay(Circle().stroke(Color.secondary, lineWidth: 1))
        }
    }
}
