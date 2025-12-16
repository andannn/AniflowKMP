// NewReleaseCardSimple.swift
// A minimal SwiftUI implementation of the Android Compose NewReleaseCard.
// This is a new file — it does not modify existing files.

import SwiftUI
import Shared

/// Simple, self-contained New Release card using native TabView paging.
/// - Parameters:
///   - items: list of MediaWithMediaListItem
///   - userTitleLanguage: language helper from Shared
///   - onItemClick: callback when user taps an item or the Detail button
struct NewReleaseCardSimple: View {
    let items: [MediaWithMediaListItem]
    let userTitleLanguage: UserTitleLanguage
    var onItemClick: (MediaWithMediaListItem) -> Void = { _ in }

    @State private var selection: Int = 0

    private let cornerRadius: CGFloat = 14
    private let bannerHeight: CGFloat = 120
    private let horizontalPadding: CGFloat = 16

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            // Header
            Label("New Release", systemImage: "sparkles")
                .font(.headline)
                .foregroundStyle(.secondary)
                .labelStyle(.titleAndIcon)

            // Carousel (TabView pager)
            if !items.isEmpty {
                // Use full-width pages to avoid overlapping previews between banners
                TabView(selection: $selection) {
                    ForEach(items.indices, id: \.self) { i in
                        let item = items[i]

                        Button(action: { onItemClick(item) }) {
                            CustomAsyncImage(url: item.mediaModel.bannerImage ?? item.mediaModel.coverImage, contentMode: .fill)
                                .frame(height: bannerHeight)
                                .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
                                .overlay {
                                    RoundedRectangle(cornerRadius: cornerRadius, style: .continuous)
                                        .strokeBorder(.secondary.opacity(0.2))
                                }
                                .contentShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
                        }
                        .buttonStyle(.plain)
                        .tag(i)
                        .padding(.horizontal, horizontalPadding)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .frame(maxWidth: .infinity)
                .frame(height: bannerHeight + 16)
                .padding(.bottom, 8)
                .animation(.easeInOut(duration: 0.2), value: selection)
            }

            // Page dots
            HStack(spacing: 8) {
                ForEach(items.indices, id: \.self) { i in
                    Circle()
                        .frame(width: selection == i ? 10 : 8, height: selection == i ? 10 : 8)
                        .foregroundStyle(selection == i ? .primary : .secondary)
                        .onTapGesture { withAnimation { selection = i } }
                }
            }
            .padding(.top, 4)

            // Title
            if items.indices.contains(selection) {
                Text(items[selection].mediaModel.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? "")
                    .font(.title3.weight(.semibold))
                    .lineLimit(2)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            // Meta row
            HStack {
                if items.indices.contains(selection) {
                    let cur = items[selection]
                    let nextEp = (cur.mediaListModel.progress?.intValue ?? 0) + 1
                    Text("Next up: Episode \(nextEp)")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                } else {
                    Text("Next up: —")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }

                Spacer()
            }
        }
        .padding(horizontalPadding)
        .padding(.vertical, 14)
        .background(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .fill(.thinMaterial)
                .shadow(color: .black.opacity(0.06), radius: 12, y: 6)
        )
    }
}
