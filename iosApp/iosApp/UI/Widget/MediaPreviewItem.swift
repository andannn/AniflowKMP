import SwiftUI

struct MediaPreviewItem: View {
    let title: String
    let isFollowing: Bool
    let coverImage: String?
    let onClick: () -> Void

    var body: some View {
        Button(action: onClick) {
            ZStack(alignment: .bottomLeading) {
                AsyncImage(url: URL(string: coverImage ?? "")) { phase in
                    switch phase {
                    case .success(let image):
                        image
                            .resizable()
                            .aspectRatio(3/4, contentMode: .fill)
                            .clipped()
                    default:
                        Color.gray
                            .aspectRatio(3/4, contentMode: .fill)
                    }
                }

                LinearGradient(
                    colors: [Color.clear, Color.black.opacity(0.8)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 50)
                .frame(maxWidth: .infinity, alignment: .bottom)
                .alignmentGuide(.bottom) { d in d[.bottom] }

                Text(title)
                    .font(.body)
                    .foregroundColor(.white)
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .padding(.leading, 8)
                    .padding(.bottom, 12)
            }
            .frame(width: 240)
            .clipShape(RoundedRectangle(cornerRadius: 24))
            .contentShape(RoundedRectangle(cornerRadius: 24))
            .overlay(alignment: .topTrailing) {
                if isFollowing {
                    ZStack {
                        Image(systemName: "bookmark.fill")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24, height: 24)
                            .scaleEffect(x: 0.9, y: 1.4)
                            .foregroundColor(.white)
                        Image(systemName: "bookmark.fill")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 24, height: 24)
                            .scaleEffect(x: 0.8, y: 1.3)
                            .foregroundColor(.orange)
                    }
                    .padding(.top, 8)
                    .padding(.trailing, 8)
                }
            }
        }
        .buttonStyle(PlainButtonStyle())
    }
}
