import SDWebImageSwiftUI
import SwiftUI

struct CustomAsyncImage: View {
    let url: String?
    var contentMode: ContentMode = .fill
    @State private var loadFailed = false

    var body: some View {
        Group {
            if loadFailed {
                failureView
            } else {
                WebImage(url: URL(string: url ?? ""))
                    .resizable()
                    .aspectRatio(contentMode: contentMode)
                    .placeholder { placeholderView }
                    .onFailure { _ in
                        loadFailed = true
                    }
                    .indicator(.activity)
            }
        }
    }

    private var placeholderView: some View {
        ZStack {
            Color(.systemGray6)
            ProgressView()
        }
    }

    private var failureView: some View {
        ZStack {
            Color(.systemGray6)
            Image(systemName: "photo")
                .foregroundColor(.gray)
        }
    }
}

#Preview {
    CustomAsyncImage(url: "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx151799-igwbH3AffgHc.jpg")
        .frame(width: 200, height: 300)
}
