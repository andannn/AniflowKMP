import SwiftUI

public struct ToggleLikeButton: View {
    public let isLiked: Bool
    public var onToggle: () -> Void
    
    @State private var animate = false
    
    
    public var body: some View {
        Button(action: {
            onToggle()
        }) {
            if isLiked {
                Image(systemName: "heart.fill")
                    .foregroundColor(.red)
            } else {
                Image(systemName: "heart")
            }
        }
        .accessibilityLabel(isLiked ? "Unlike" : "Like")
        .accessibilityValue(isLiked ? "Liked" : "Not liked")
    }
}
