import SwiftUI
import Shared

@MainActor
class TrackProgressDialogViewModel: ObservableObject {
    let mediaId: String
    
    private let dataProvider: TrackProgressDialogDataProvider
    @Published var uiData: TrackProgressDialogState = TrackProgressDialogState.companion.Empty
    private var dataTask:  Task<(), any Error>? = nil

    init(mediaId: String) {
        self.mediaId = mediaId
        
        dataProvider = KoinHelper.shared.trackProgressDialogDataProvider(mediaId: mediaId)
        
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.uiDataAsyncSequence() else { return }
            for try await data in stream {
                self?.uiData = data
            }
        }
    }
    
    deinit {
        print("TrackProgressDialogViewModel deinit")
        dataTask?.cancel()
    }
}

struct TrackProgressDialogContainer: View {
    @StateObject private var viewModel: TrackProgressDialogViewModel
    let onSave: (Int) -> Void

    init(mediaId: String, onSave: @escaping (Int) -> Void = { _ in }) {
        _viewModel = StateObject(wrappedValue: TrackProgressDialogViewModel(mediaId: mediaId))
        self.onSave = onSave
    }
    
    var body: some View {
        TrackProgressDialog(
            initialProgress: Int(viewModel.uiData.initialProgress),
            maxEpisodes: viewModel.uiData.maxEp.intOrNil,
            onSave: onSave
        )
    }
}

struct TrackProgressDialog: View {
    @State private var value: Int
    let initialProgress: Int
    let maxEpisodes: Int?
    let onSave: (Int) -> Void

    @FocusState private var isTextFieldFocused: Bool

    init(initialProgress: Int, maxEpisodes: Int?, onSave: @escaping (Int) -> Void = { _ in }) {
        self.initialProgress = initialProgress
        self.maxEpisodes = maxEpisodes
        self.onSave = onSave
        _value = State(initialValue: initialProgress)
    }

    var hasNext: Bool {
        if let max = maxEpisodes {
            return value < max
        }
        return true
    }

    var hasPrev: Bool {
        value > 0
    }

    func safeUpdateProgress(_ progress: Int) {
        if let maxEp = maxEpisodes {
            value = min(max(progress, 0), maxEp)
        } else {
            value = max(progress, 0)
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            Text("Track Progress")
                .font(.title2)
                .fontWeight(.bold)
                .multilineTextAlignment(.center)
                .padding(.top, 8)

            HStack(spacing: 8) {
                Button(action: {
                    safeUpdateProgress(value - 1)
                }) {
                    Text("−1")
                }
                .frame(height: 56)
                .disabled(!hasPrev)
                .padding(.trailing, 8)

                ZStack(alignment: .trailing) {
                    TextField(
                        "",
                        value: $value,
                        formatter: NumberFormatter(),
                        onEditingChanged: { _ in },
                        onCommit: { onSave(value) }
                    )
                    .keyboardType(.numberPad)
                    .focused($isTextFieldFocused)
                    .font(.system(size: 22, weight: .semibold, design: .rounded))
                    .padding(.horizontal, 24)
                    .padding(.vertical, 10)
                    .frame(width: 70)
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color.accentColor, lineWidth: 2)
                    )
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)
                    .onAppear {
                        isTextFieldFocused = true
                    }
                    if let max = maxEpisodes {
                        Text("/\(max)")
                            .foregroundColor(.secondary)
                            .font(.system(size: 16, weight: .regular))
                    }
                }
                .frame(height: 56)

                Button(action: {
                    safeUpdateProgress(value + 1)
                }) {
                    Text("+1")
                }
                .frame(height: 56)
                .disabled(!hasNext)
                .padding(.leading, 8)

            }
            .padding(.horizontal)
        }
    }
}

extension KotlinInt? {
    var intOrNil: Int? {
        if let self {
            return Int(truncating: self)
        }
        return nil
    }
}
