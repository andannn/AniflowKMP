import SwiftUI

struct CustomAsyncImage: View {
    let url: String?
    var contentMode: ContentMode = .fill
    
    var body: some View {
        AsyncImage(url: URL(string: url ?? "")) { phase in
            switch phase {
            case .empty:
                ZStack {
                    Color(.systemGray6)
                    ProgressView()
                }
            case .success(let image):
                image
                    .resizable()
                    .aspectRatio(contentMode: contentMode)
            case .failure(_):
                ZStack {
                    Color(.systemGray6)
                    Image(systemName: "photo")
                        .foregroundColor(.gray)
                }
            @unknown default:
                Color(.systemGray6)
            }
        }
    }
}

#Preview {
    CustomAsyncImage(url: "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx151799-igwbH3AffgHc.jpg")
        .frame(width: 200, height: 300)
}
