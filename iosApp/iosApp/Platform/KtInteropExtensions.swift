
import Shared
import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCombine
import Combine
import Foundation
import KMPNativeCoroutinesAsync

extension PageComponent {
    
    func pageItems()
    -> NativeFlowAsyncSequence<Any, Error, KotlinUnit> {
        asyncSequence(for: items)
    }
    
    func pageStaus()
    -> NativeFlowAsyncSequence<LoadingStatus, Error, KotlinUnit> {
        asyncSequence(for: status)
    }
}

extension DiscoverUiDataProvider {
    func getdiscoverUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<DiscoverUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func discoverUiSideEffectStatusSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension DetailStaffUiDataProvider {
    func uiStateAsyncSequence()
    -> NativeFlowAsyncSequence<DetailStaffUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func uiSideEffectStatusSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension TrackUiDataProvider {
    func gettrackUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<TrackUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func trackUiSideEffectErrorSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension HomeAppBarUiDataProvider {
    func appBarAsyncSequence()
    -> NativeFlowAsyncSequence<HomeAppBarUiState, Error, KotlinUnit> {
        asyncSequence(for: appBarFlow())
    }
}

extension DetailMediaUiDataProvider {
    func detailUiDataFlowAsyncSequence()
    -> NativeFlowAsyncSequence<DetailUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func detailUiSideEffectAsyncSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension DetailCharacterUiDataProvider {
    func uiDataFlowAsyncSequence()
    -> NativeFlowAsyncSequence<DetailCharacterUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func uiSideEffectAsyncSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension SettingUiDataProvider {
    func settingUiDataFlowAsyncSequence()
    -> NativeFlowAsyncSequence<SettingUiState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
    
    func settingUiSideEffectAsyncSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: uiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension MediaRepository {
    func setContentMode(mode: MediaContentMode) async throws {
        try await asyncFunction(for: setContentMode(mode: mode))
    }

    func updateMediaListProgress(mediaListId: String, progress: Int? = nil, score: Float? = nil, status: MediaListStatus? = nil) async throws -> AppError? {
        let progress = progress.map { KotlinInt(integerLiteral: $0) }
        let kScore: KotlinFloat?   = score.map { KotlinFloat(floatLiteral: Double($0)) }

        return try await asyncFunction(for: updateMediaListStatus(mediaListId: mediaListId, status: status, progress: progress, score: kScore))
    }
}

extension FetchNotificationTask {
    func sync() async throws -> SyncResult {
        return try await asyncFunction(for: sync())
    }
}

extension AuthRepository {
    func getAuthedUserAsyncSequence()
    -> NativeFlowAsyncSequence<Optional<UserModel>, Error, KotlinUnit> {
        asyncSequence(for: getAuthedUserFlow())
    }
    
    func startLoginProcessAndWaitResult() async throws -> AppError? {
        try await asyncFunction(for: startLoginProcessAndWaitResult())
    }
    
    func getUserOptionsAsyncSequence()
    -> NativeFlowAsyncSequence<UserOptions, Error, KotlinUnit> {
        asyncSequence(for: getUserOptionsFlow())
    }
    
}

extension AppErrorSource {
    func getErrorAsyncSequence() -> NativeFlowAsyncSequence<[AppError], Error, KotlinUnit> {
        asyncSequence(for: errorSharedFlow)
    }
}
