import SwiftUI

public struct SectionHeader: View {
    public var title: String
    public var showMore: Bool = false
    public var onMore: () -> Void = {}
    
    public var body: some View {
        HStack {
            Text(title)
                .font(.headline)
                .foregroundColor(.primary)
            Spacer()
            if showMore {
                Button(action: onMore) {
                    Label("更多", systemImage: "chevron.right")
                        .labelStyle(.titleAndIcon)
                        .font(.subheadline)
                        .foregroundColor(.accentColor)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 4)
        .background(Color(.systemBackground))
    }
}
