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
                            .aspectRatio(3/4, contentMode: .fit)
                            .clipped()
                    default:
                        Color.gray
                            .aspectRatio(3/4, contentMode: .fit)
                    }
                }

                LinearGradient(
                    colors: [Color.clear, Color.black.opacity(0.8)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 50)
                .alignmentGuide(.bottom) { d in d[.bottom] }

                Text(title)
                    .font(.body)
                    .foregroundColor(.white)
                    .lineLimit(1)
                    .truncationMode(.tail)
                    .padding(.leading, 8)
                    .padding(.bottom, 12)
            }
            .clipShape(RoundedRectangle(cornerRadius: 24))
            .contentShape(RoundedRectangle(cornerRadius: 24))
        }
        .buttonStyle(PlainButtonStyle())
    }
}

#Preview
{
    MediaPreviewItem(
        title: "Title",
        isFollowing: false,
        coverImage: "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx151799-igwbH3AffgHc.jpg",
        onClick: {}
    )
}

