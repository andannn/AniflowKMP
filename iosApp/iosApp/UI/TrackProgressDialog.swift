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
        
        dataProvider = KoinExtension.shared.trackProgressDialogDataProvider(mediaId: mediaId)
        
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
        let initial = Int(viewModel.uiData.initialProgress)
        TrackProgressDialog(
            initialProgress: initial,
            maxEpisodes: viewModel.uiData.maxEp.intOrNil,
            onSave: onSave
        ).id(initial)
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
        VStack(spacing: 24) {
            // Title and close button
            HStack {
                Spacer()
                Text("Track Progress")
                    .font(.title2)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.center)
                Spacer()
            }
            .padding(.top, 8)
            .padding(.horizontal)

            // Progress controls
            HStack(spacing: 16) {
                Button(action: {
                    safeUpdateProgress(value - 1)
                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                }) {
                    Image(systemName: "minus.circle.fill")
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(hasPrev ? .accentColor : .gray)
                }
                .buttonStyle(.plain)
                .disabled(!hasPrev)
                .accessibilityLabel("Decrease progress")

                ZStack(alignment: .trailing) {
                    TextField("0", value: $value, formatter: NumberFormatter(), onEditingChanged: { _ in }, onCommit: { onSave(value) })
                        .keyboardType(.numberPad)
                        .focused($isTextFieldFocused)
                        .font(.system(size: 22, weight: .semibold, design: .rounded))
                        .frame(width: 60, height: 44)
                        .padding(.horizontal, 8)
                        .background(Color(.systemGray6))
                        .cornerRadius(10)
                        .overlay(
                            RoundedRectangle(cornerRadius: 10)
                                .stroke(Color.secondary, lineWidth: 1)
                        )
                        .multilineTextAlignment(.center)
                        .onAppear {
                            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                                isTextFieldFocused = true
                            }
                        }
                    if let max = maxEpisodes {
                        Text("/\(max)")
                            .foregroundColor(.secondary)
                            .font(.system(size: 16, weight: .regular))
                            .padding(.trailing, 4)
                    }
                }

                Button(action: {
                    safeUpdateProgress(value + 1)
                    UIImpactFeedbackGenerator(style: .light).impactOccurred()
                }) {
                    Image(systemName: "plus.circle.fill")
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(hasNext ? .accentColor : .gray)
                }
                .buttonStyle(.plain)
                .disabled(!hasNext)
                .accessibilityLabel("Increase progress")
            }
            .padding(.horizontal)

            // Save button
            Button(action: {
                onSave(value)
            }) {
                Text("Save")
                    .font(.headline)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.accentColor)
            .padding(.horizontal)
            .padding(.top, 8)
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
