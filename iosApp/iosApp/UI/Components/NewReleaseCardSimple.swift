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
    private let bannerHeight: CGFloat = 110

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header
            HStack {
                Text("New Release")
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.secondary)
                Spacer()
            }

            // Carousel (TabView pager)
            if !items.isEmpty {
                // Keep pages simple: center a bounded-width banner in each page to avoid overlapping
                TabView(selection: $selection) {
                    ForEach(items.indices, id: \.self) { i in
                        let item = items[i]

                        HStack {
                            Spacer()
                            Button(action: { onItemClick(item) }) {
                                CustomAsyncImage(url: item.mediaModel.bannerImage ?? item.mediaModel.coverImage, contentMode: .fill)
                                    .frame(height: bannerHeight)
                                    .frame(maxWidth: 320)
                                    .clipShape(RoundedRectangle(cornerRadius: cornerRadius, style: .continuous))
                            }
                            .buttonStyle(.plain)
                            Spacer()
                        }
                        .tag(i)
                        .padding(.horizontal, 6)
                    }
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
                .frame(height: bannerHeight)
                .padding(.bottom, 8)
            }

            // Page dots
            HStack(spacing: 6) {
                ForEach(items.indices, id: \.self) { i in
                    Circle()
                        .frame(width: 8, height: 8)
                        .opacity(selection == i ? 1 : 0.3)
                        .onTapGesture { withAnimation { selection = i } }
                }
            }
            .padding(.top, 6)

            // Title
            if items.indices.contains(selection) {
                Text(items[selection].mediaModel.title?.getUserTitleString(titleLanguage: userTitleLanguage) ?? "")
                    .font(.system(size: 20, weight: .semibold))
                    .lineLimit(2)
            }

            // Meta row
            HStack {
                if items.indices.contains(selection) {
                    let cur = items[selection]
                    let nextEp = (cur.mediaListModel.progress?.intValue ?? 0) + 1
                    Text("Next up: Episode \(nextEp)")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                } else {
                    Text("Next up: —")
                        .font(.system(size: 14))
                        .foregroundColor(.secondary)
                }

                Spacer()
            }
        }
        .padding(12)
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 18, style: .continuous))
        .overlay(
            RoundedRectangle(cornerRadius: 18, style: .continuous)
                .stroke(Color(UIColor.separator).opacity(0.6), lineWidth: 1)
        )
    }
}
