
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
    
    func discoverUiSideEffectStatusSequence()
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: discoverUiSideEffect())
    }
    
}

extension TrackUiDataProvider {
    func gettrackUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<TrackUiState, Error, KotlinUnit> {
        asyncSequence(for: trackUiDataFlow())
    }
    
    func trackUiSideEffectErrorSequence()
    -> NativeFlowAsyncSequence<SyncStatus, Error, KotlinUnit> {
        asyncSequence(for: trackUiSideEffect())
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
}
