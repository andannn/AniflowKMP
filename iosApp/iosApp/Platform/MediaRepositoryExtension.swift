
import Shared
import KMPNativeCoroutinesAsync

extension DataProvider {
    func getdiscoverUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<DiscoverUiState, Error, KotlinUnit> {
        asyncSequence(for: discoverUiDataFlow())
    }
    
    func discoverUiSideEffectStatusSequence()
    -> NativeFlowAsyncSequence<RefreshStatus, Error, KotlinUnit> {
        asyncSequence(for: discoverUiSideEffect())
    }
    
    
    func gettrackUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<TrackUiState, Error, KotlinUnit> {
        asyncSequence(for: trackUiDataFlow())
    }
    
    func trackUiSideEffectErrorSequence()
    -> NativeFlowAsyncSequence<AppError, Error, KotlinUnit> {
        asyncSequence(for: trackUiSideEffect())
    }
}
