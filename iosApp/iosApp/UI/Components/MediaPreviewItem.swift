import SwiftUI

struct MediaPreviewItem: View {
    let title: String
    let coverImage: String?
    let onClick: () -> Void

    var body: some View {
        GeometryReader { geo in
            let width = geo.size.width
            let height = width * 4.0 / 3.0 // maintain 3:4 (width:height = 3:4)

            ZStack(alignment: .bottomLeading) {
                CustomAsyncImage(url: coverImage, contentMode: .fill)
                    .frame(width: width, height: height)
                    .clipped()

                // subtle bottom gradient to improve text contrast
                LinearGradient(
                    colors: [Color.clear, Color.black.opacity(0.6)],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: min(64, height * 0.35))
                .frame(maxWidth: .infinity, alignment: .bottom)

                // Title
                Text(title)
                    .font(.subheadline).fontWeight(.semibold)
                    .foregroundColor(.white)
                    .lineLimit(1)
                    .padding(.leading, 12)
                    .padding(.bottom, 10)
            }
            .background(Color(.systemBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .shadow(color: Color.black.opacity(0.08), radius: 4, x: 0, y: 2)
            .contentShape(Rectangle())
            .onTapGesture(perform: onClick)
            .accessibilityElement(children: .combine)
            .accessibilityLabel(Text(title))
        }
        .aspectRatio(3.0/4.0, contentMode: .fit) // ensure parent layout reserves proper space
    }
}
