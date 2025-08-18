
import Shared
import KMPNativeCoroutinesAsync

class DataProviderWrapper {
    private let ktDataProvider: DataProvider
    
    init(ktDataProvider: DataProvider) {
        self.ktDataProvider = ktDataProvider
    }
    
    @available(iOS 13.0, *)
    func getdiscoverUiStateAsyncSequence()
    -> NativeFlowAsyncSequence<DataWithError<DiscoverUiState>, Error, KotlinUnit> {
        asyncSequence(for: ktDataProvider.discoverUiDataFlow())
    }
}
