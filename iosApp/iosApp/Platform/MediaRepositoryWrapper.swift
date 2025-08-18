
import Shared
import KMPNativeCoroutinesAsync

class MediaRepositoryWrapper {
    private let ktRepository: MediaRepository
    
    init(ktRepository: MediaRepository) {
        self.ktRepository = ktRepository
    }
    
    @available(iOS 13.0, *)
    func getMediaAsyncSequence(category: MediaCategory)
    -> NativeFlowAsyncSequence<DataWithError<CategoryWithContents>, Error, KotlinUnit> {
        asyncSequence(for: ktRepository.getMediasFlow(category: category))
    }
}
