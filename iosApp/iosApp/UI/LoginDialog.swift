import Shared
import SwiftUI

@MainActor
class LoginDialogViewModel : ObservableObject {
    
    private let authRepository: AuthRepository
    @Published  var user: UserModel? = nil
    
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
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 12) {
                Button(action: {}) {
                    avatarView
                }
                
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
                .buttonStyle(.bordered)
            } else {
                Button("Login with AniList", action: {
                    viewModel.startLoginProcess()
                })
                .buttonStyle(.bordered)
            }
        }
        .padding(16)
        .frame(maxWidth: 320)
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
            .frame(width: 40, height: 40)
            .clipShape(Circle())
        } else {
            Image(systemName: "person.crop.circle")
                .resizable().scaledToFit()
                .frame(width: 40, height: 40)
        }
    }
}
