import Shared
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
