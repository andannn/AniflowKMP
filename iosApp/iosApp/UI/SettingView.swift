import SwiftUI

import Shared

@MainActor
class SettingViewModel: ObservableObject {
    @Published public var uiState: SettingUiState = SettingUiState.companion.Empty
    
    private let dataProvider: SettingUiDataProvider
    private let authRep: AuthRepository = KoinHelper.shared.authRepository()

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
    
    func onChangeSetting(option: SettingOption) {
        Task {
            var error: AppError? = nil
            switch option {
            case let option as SettingOptionScoreFormatOption:
                error = try await authRep.updateUserSettings(scoreFormat: option.value)
            case let option as SettingOptionThemeModeOption:
                error = try await authRep.updateUserSettings(appTheme: option.value)
            case let option as SettingOptionUserTitleLanguageOption:
                error = try await authRep.updateUserSettings(titleLanguage: option.value)
            case let option as SettingOptionStaffCharacterNameOption:
                error = try await authRep.updateUserSettings(staffCharacterNameLanguage: option.value)
            default:
                print("Unknown option type")
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
    @State private var selectedSetting: SettingItemWrapper? = nil
    @State private var showing: Bool = false

    var body: some View {
        List {
            ForEach(viewModel.uiState.settingGroupList, id: \.title) { group in
                Section(header: Text(group.title)) {
                    ForEach(Array(group.settings.enumerated()), id: \.offset) { index, item in
                        if let singleSelect = item as? SettingItemSingleSelect {
                            SettingSingleSelectRow(
                                item: singleSelect,
                                onClick: {
                                    showing = true
                                    selectedSetting = SettingItemWrapper(item: singleSelect)
                                }
                            )
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
        .confirmationDialog(
            selectedSetting?.item.title ?? "",
            isPresented: Binding(
                get: { selectedSetting != nil },
                set: { if !$0 { selectedSetting = nil } }
            ),
            actions: {
                if let singleSelect = selectedSetting?.item as? SettingItemSingleSelect {
                    ForEach(singleSelect.options.indices, id: \.self) { idx in
                        let option = singleSelect.options[idx]
                        Button(option.label) {
                            viewModel.onChangeSetting(option: option)
                            selectedSetting = nil
                        }
                    }
                } else {
                    Button("OK", role: .cancel) { selectedSetting = nil }
                }
            }
        )
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

struct SettingItemWrapper: Identifiable {
    let item: SettingItem
    let id = UUID()
}
