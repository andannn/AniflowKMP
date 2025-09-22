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
    
    func startLoginProcess() {
        print("LoginDialogViewModel startLoginProcess ")
        loginTask?.cancel()
        loginTask = Task {
            print("LoginDialogViewModel start ")
            let appError = try await authRepository.startLoginProcessAndWaitResult()
            print("LoginDialogViewModel end appError? \(String(describing: appError?.message)) ")
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
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
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
            
            Divider()
            
            if viewModel.user != nil {
                Button("Logout", role: .destructive, action: {
                })
                .buttonStyle(.borderedProminent)
                .tint(.red)
                .frame(maxWidth: .infinity)
            } else {
                Button("Login with AniList", action: {
                    viewModel.startLoginProcess()
                })
                .buttonStyle(.borderedProminent)
                .tint(.blue)
                .frame(maxWidth: .infinity)
            }
        }
        .padding(20)
        .frame(maxWidth: 340)
        .padding(.horizontal, 24)
    }
    
    @ViewBuilder
    private var avatarView: some View {
        if let urlStr = viewModel.user?.avatar, let url = URL(string: urlStr) {
            AsyncImage(url: url) { phase in
                switch phase {
                case .success(let image):
                    image.resizable().aspectRatio(contentMode: .fill)
                case .failure(_):
                    Image(systemName: "person.crop.circle")
                        .resizable().scaledToFit().padding(4)
                default:
                    ProgressView()
                }
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
