import SwiftUI
import Shared

struct CharacterWithMediaItem: View {
    let item: VoicedCharacterWithMedia
    let userTitleLanguage: UserTitleLanguage
    let userStaffLanguage: UserStaffNameLanguage
    let onCharacterClick: () -> Void
    let onMediaClick: () -> Void
    
    init(
        item: VoicedCharacterWithMedia,
        userTitleLanguage: UserTitleLanguage,
        userStaffLanguage: UserStaffNameLanguage,
        onCharacterClick: @escaping () -> Void = {},
        onMediaClick: @escaping () -> Void = {}
    ) {
        self.item = item
        self.userTitleLanguage = userTitleLanguage
        self.userStaffLanguage = userStaffLanguage
        self.onCharacterClick = onCharacterClick
        self.onMediaClick = onMediaClick
    }
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Image area: main character image with a small media cover overlayed at bottom-right
            ZStack(alignment: .bottomTrailing) {
                CustomAsyncImage(url: item.character.image)
                    .scaledToFill()
                    .frame(height: 240)
                    .frame(maxWidth: .infinity)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 16))
                    .contentShape(RoundedRectangle(cornerRadius: 16))
                    .onTapGesture(perform: onCharacterClick)

                // Small media cover
                CustomAsyncImage(url: item.media.coverImage)
                    .scaledToFill()
                    .frame(width: 86, height: 114)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .shadow(color: Color.black.opacity(0.25), radius: 4, x: 0, y: 2)
                    .padding(4)
                    .onTapGesture(perform: onMediaClick)
            }

            // Text area
            VStack(alignment: .leading, spacing: 4) {
                Text(item.character.name?.getNameString(staffName: userStaffLanguage) ?? "")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(.primary)
                    .lineLimit(1)

                Text(item.media.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? "")
                    .font(.system(size: 15))
                    .foregroundStyle(.secondary)
                    .lineLimit(1)
            }
            .padding(.horizontal, 12)
            .padding(.bottom, 12)
        }
        .background(Color(.secondarySystemGroupedBackground))
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
}

// Helper for custom corner radius (kept for compatibility with other files)
struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}
