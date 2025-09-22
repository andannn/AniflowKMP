import SwiftUI

import Shared

@MainActor
class SettingViewModel: ObservableObject {
    @Published public var uiState: SettingUiState = SettingUiState.companion.Empty
    
    private let dataProvider: SettingUiDataProvider

    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    
    init() {
        dataProvider = KoinHelper.shared.settingUiDataProvider()
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.settingUiDataFlowAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.settingUiDataFlowAsyncSequence() else { return }
            
            for try await _ in stream {
                // no-op
            }
        }
    }
    
    deinit {
        dataTask?.cancel()
        sideEffectTask?.cancel()
    }
}

struct SettingView: View {
    @StateObject private var viewModel = SettingViewModel()
    
    var body: some View {
        List {
            ForEach(viewModel.uiState.settingGroupList, id: \.title) { group in
                Section(header: Text(group.title)) {
                    ForEach(Array(group.settings.enumerated()), id: \.offset) { index, item in
                        if let singleSelect = item as? SettingItemSingleSelect {
                            SettingSingleSelectRow(item: singleSelect)
                        } else {
                            // Handle other types if needed
                            EmptyView()
                        }
                    }
                }
            }
        }
        .listStyle(InsetGroupedListStyle())
        .navigationTitle("Setting")
    }
}

struct SettingSingleSelectRow: View {
    let item: SettingItemSingleSelect
    var onClick: (() -> Void)? = nil
    
    var body: some View {
        Button(action: { onClick?() }) {
            VStack(alignment: .leading, spacing: 2) {
                Text(item.title)
                    .font(.body)
                if let subtitle = item.subtitle, !subtitle.isEmpty {
                    Text(subtitle)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Text(item.selectedOption.label)
                    .font(.subheadline)
                    .foregroundColor(.accentColor)
            }
            .padding(.vertical, 4)
        }
        .buttonStyle(PlainButtonStyle())
    }
}
