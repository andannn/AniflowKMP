
import Shared
import KMPNativeCoroutinesAsync

class DataProviderWrapper {
    private let ktDataProvider: DataProvider
    
    init(ktDataProvider: DataProvider) {
        self.ktDataProvider = ktDataProvider
    }
    
    @available(iOS 13.0, *)
    func getdiscoverUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<DiscoverUiState, Error, KotlinUnit> {
        asyncSequence(for: ktDataProvider.discoverUiDataFlow())
    }
    
    @available(iOS 13.0, *)
    func discoverUiSideEffectStatusSequence()
    -> NativeFlowAsyncSequence<RefreshStatus, Error, KotlinUnit> {
        asyncSequence(for: ktDataProvider.discoverUiSideEffect())
    }
    
    
    @available(iOS 13.0, *)
    func gettrackUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<TrackUiState, Error, KotlinUnit> {
        asyncSequence(for: ktDataProvider.trackUiDataFlow())
    }
    
    @available(iOS 13.0, *)
    func trackUiSideEffectErrorSequence()
    -> NativeFlowAsyncSequence<AppError, Error, KotlinUnit> {
        asyncSequence(for: ktDataProvider.trackUiSideEffect())
    }
    
}
