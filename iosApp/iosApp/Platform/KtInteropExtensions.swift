
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
        asyncSequence(for: discoverUiDataFlow())
    }
    
    func discoverUiSideEffectStatusSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: discoverUiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension TrackUiDataProvider {
    func gettrackUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<TrackUiState, Error, KotlinUnit> {
        asyncSequence(for: trackUiDataFlow())
    }
    
    func trackUiSideEffectErrorSequence(_ forceRefreshFirstTime: Bool)
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: trackUiSideEffect(forceRefreshFirstTime: forceRefreshFirstTime))
    }
}

extension HomeAppBarUiDataProvider {
    func appBarFlow()
    -> NativeFlowAsyncSequence<HomeAppBarUiState, Error, KotlinUnit> {
        asyncSequence(for: appBarFlow())
    }
}

extension MediaRepository {
    func setContentMode(mode: MediaContentMode) async throws {
        try await asyncFunction(for: setContentMode(mode: mode))
    }
}

extension AuthRepository {
    func getAuthedUserFlow()
    -> NativeFlowAsyncSequence<Optional<UserModel>, Error, KotlinUnit> {
        asyncSequence(for: getAuthedUserFlow())
    }
    
    func startLoginProcessAndWaitResult() async throws -> AppError? {
        try await asyncFunction(for: startLoginProcessAndWaitResult())
    }
    
    func getUserOptionsFlow()
    -> NativeFlowAsyncSequence<UserOptions, Error, KotlinUnit> {
        asyncSequence(for: getUserOptionsFlow())
    }
    
}
