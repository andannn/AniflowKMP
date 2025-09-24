import SwiftUI

struct AvatarButton: View {
    let avatarUrl: String?
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            if let avatarUrl = avatarUrl {
                CustomAsyncImage(url: avatarUrl, contentMode: .fill)
                    .frame(width: 32, height: 32)
                    .clipShape(Circle())
                    .overlay(
                        Circle()
                            .stroke(Color(.systemGray5), lineWidth: 1)
                    )
            } else {
                Image(systemName: "person.crop.circle")
                    .font(.system(size: 24))
                    .foregroundColor(.primary)
            }
        }
        .buttonStyle(.plain)
        .accessibilityLabel("User Profile")
    }
}
