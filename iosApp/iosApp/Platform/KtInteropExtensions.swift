
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

extension TrackProgressDialogDataProvider {
    func uiDataAsyncSequence()
    -> NativeFlowAsyncSequence<TrackProgressDialogState, Error, KotlinUnit> {
        asyncSequence(for: uiDataFlow())
    }
}

extension MediaRepository {
    func setContentMode(mode: MediaContentMode) async throws {
        try await asyncFunction(for: setContentMode(mode: mode))
    }

    func updateMediaListStatus(mediaListId: String, progress: Int? = nil, score: Float? = nil, status: MediaListStatus? = nil) async throws -> AppError? {
        let progress = progress.map { KotlinInt(integerLiteral: $0) }
        let kScore: KotlinFloat?   = score.map { KotlinFloat(floatLiteral: Double($0)) }

        return try await asyncFunction(for: updateMediaListStatus(mediaListId: mediaListId, status: status, progress: progress, score: kScore))
    }
    
    func addNewMediaToList(mediaId: String) async throws -> AppError? {
        return try await asyncFunction(for: addNewMediaToList(mediaId: mediaId))
    }
    
    func toggleMediaItemLike(mediaId: String, mediaType: MediaType) async throws -> AppError? {
        return try await asyncFunction(for: toggleMediaItemLike(mediaId: mediaId, mediaType: mediaType))
    }
    
    func toggleCharacterFavorite(characterId: String) async throws -> AppError? {
        return try await asyncFunction(for: toggleCharacterItemLike(characterId: characterId))
    }
    
    func toggleStaffFavorite(staffId: String) async throws -> AppError? {
        return try await asyncFunction(for: toggleStaffItemLike(staffId: staffId))
    }
}

extension FetchNotificationTask {
    func sync() async throws -> SyncResult {
        return try await asyncFunction(for: sync())
    }
}

extension MarkProgressUseCase {
    func markProgress(
        mediaListModel: MediaListModel,
        mediaModel: MediaModel,
        newProgress: Int32,
        snackBarMessageHandler: any SnackBarMessageHandler,
        errorHandler: any AppErrorHandler
    ) async throws  {
        try await asyncFunction(for: markProgress(mediaListModel: mediaListModel, mediaModel: mediaModel, newProgress: newProgress, snackBarMessageHandler: snackBarMessageHandler, errorHandler: errorHandler))
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
    
    func updateUserSettings(
         titleLanguage: UserTitleLanguage? = nil,
         staffCharacterNameLanguage: UserStaffNameLanguage? = nil,
         appTheme: Theme? = nil,
         scoreFormat: ScoreFormat? = nil
    ) async throws -> AppError? {
        try await asyncFunction(
            for: updateUserSettings(titleLanguage: titleLanguage, staffCharacterNameLanguage: staffCharacterNameLanguage, appTheme: appTheme, scoreFormat: scoreFormat))
    }
    
    func logout() async throws {
        try await asyncFunction(for: logout())
    }
    
}

extension AppErrorSource {
    func getErrorAsyncSequence() -> NativeFlowAsyncSequence<[AppError], Error, KotlinUnit> {
        asyncSequence(for: errorSharedFlow)
    }
}
