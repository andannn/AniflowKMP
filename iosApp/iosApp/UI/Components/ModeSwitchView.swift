import SwiftUI

struct ModeSwitchView: View {
    let isAnime: Binding<Bool>

    var body: some View {
        Picker("Content type", selection: isAnime) {
            Label("Anime", systemImage: "film.fill")
                .tag(true)
            Label("Manga", systemImage: "book.closed.fill")
                .tag(false)
        }
        .pickerStyle(.segmented)
        .frame(width: 160)
        .accessibilityHint("Switch between anime and manga content")
    }
}
