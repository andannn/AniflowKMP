
import SwiftUI
import Shared

struct MediaRelationItem: View {
    let userTitleLanguage: UserTitleLanguage
    let mediaRelation: MediaModelWithRelationType
    var onClick: () -> Void = {}
    var body: some View {
        Button(action: onClick) {
            HStack(spacing: 12) {
                // Cover Image
                if let cover = mediaRelation.media.coverImage, let url = URL(string: cover) {
                    AsyncImage(url: url) { phase in
                        switch phase {
                        case .empty:
                            ZStack {
                                Rectangle().fill(Color.gray.opacity(0.15))
                                ProgressView()
                            }
                        case .success(let img):
                            img.resizable().scaledToFill()
                        case .failure:
                            Rectangle().fill(Color.gray.opacity(0.15))
                        @unknown default:
                            Rectangle().fill(Color.gray.opacity(0.15))
                        }
                    }
                    .aspectRatio(3/4, contentMode: .fit)
                    .frame(width: 72, height: 96)
                    .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                }
                // Info Column
                VStack(alignment: .leading, spacing: 6) {
                    Text(mediaRelation.relationType.label())
                        .font(.caption)
                        .foregroundColor(.accentColor)
                        .lineLimit(1)
                        .truncationMode(.tail)
                    let title = mediaRelation.media.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? ""
                    Text(title)
                        .font(.headline)
                        .foregroundColor(.primary)
                        .lineLimit(2)
                        .truncationMode(.tail)
                    Spacer(minLength: 0)
                    Text(mediaRelation.media.infoString())
                        .font(.footnote)
                        .foregroundColor(.secondary)
                        .lineLimit(1)
                        .truncationMode(.tail)
                }
                .frame(maxHeight: 96)
                Spacer(minLength: 0)
            }
            .padding(12)
            .frame(minWidth: 200, maxWidth: 300, minHeight: 96, maxHeight: 120)
            .background(
                RoundedRectangle(cornerRadius: 16, style: .continuous)
                    .fill(Color(.systemBackground))
            )
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            .shadow(color: Color.black.opacity(0.06), radius: 4, y: 2)
        }
        .buttonStyle(.plain)
    }
}
