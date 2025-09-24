import SwiftUI

struct ModeSwitchView: View {
    let isAnime: Binding<Bool>
    
    var body: some View {
        HStack(spacing: 0) {
            ModeSwitchButton(
                isSelected: isAnime.wrappedValue,
                icon: "film.fill",
                label: "Anime",
                action: { isAnime.wrappedValue = true }
            )
            ModeSwitchButton(
                isSelected: !isAnime.wrappedValue,
                icon: "book.closed.fill",
                label: "Manga",
                action: { isAnime.wrappedValue = false }
            )
        }
        .background(Color(.systemGray6))
        .cornerRadius(6)
        .frame(width: 120, height: 32)
        .accessibilityHint("Switch between anime and manga content")
    }
}

private struct ModeSwitchButton: View {
    let isSelected: Bool
    let icon: String
    let label: String
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            HStack(spacing: 4) {
                Image(systemName: icon)
                    .font(.system(size: 12, weight: .medium))
                Text(label)
                    .font(.system(size: 11, weight: .medium))
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(isSelected ? Color.accentColor : Color.clear)
            .foregroundColor(isSelected ? .white : .primary)
        }
        .buttonStyle(.plain)
        .animation(.easeInOut(duration: 0.2), value: isSelected)
        .accessibilityLabel(label)
    }
}
