import SDWebImageSwiftUI
import SwiftUI

struct CustomAsyncImage: View {
    let url: String?
    var contentMode: ContentMode = .fill
    
    @State private var loadFailed = false
    
    var body: some View {
        let resolvedURL = URL(string: url ?? "")
        
        WebImage(
            url: resolvedURL,
            options: [.retryFailed, .scaleDownLargeImages]
        )
        .placeholder { placeholder }
        .onFailure { _ in loadFailed = true }
        .onSuccess { _, _, _ in loadFailed = false }
        .resizable()
        .indicator(.activity)
        .transition(.fade(duration: 0.2))
        .aspectRatio(contentMode: contentMode)
        .background(Color(.systemGray6))
        .overlay(failureOverlay.opacity(loadFailed ? 1 : 0))
        .clipped()
    }
    
    private var placeholder: some View {
        ZStack {
            Color(.systemGray6)
            ProgressView()
        }
    }
    
    private var failureOverlay: some View {
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
