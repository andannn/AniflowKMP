import Shared
import SwiftUI

@MainActor
class PagingViewModel<T>: ObservableObject {
    private var pageComponent: PageComponent
    @Published public var pageItems: Array<T> = []
    
    init(_ pageComponent: PageComponent) {
        self.pageComponent = pageComponent
        
        Task {
            do {
                for try await items in pageComponent.pageItems() {
                    pageItems = items as! Array<T>
                }
            } catch {
                print("Failed with error: \(error)")
            }
        }
        
    }
    
    deinit {
        pageComponent.dispose()
    }
}

struct VerticalGridPaging<T, ItemView: View>: View {
    @StateObject private var viewModel: PagingViewModel<T>
    
    let columns: [GridItem]
    var contentPadding: EdgeInsets
    let key: (T) -> AnyHashable
    let itemContent: (T) -> ItemView
    
    init(
        pageComponent: PageComponent,
        columns: [GridItem],
        contentPadding: EdgeInsets = .init(),
        key: @escaping (T) -> AnyHashable,
        @ViewBuilder itemContent: @escaping (T) -> ItemView
    ) {
        _viewModel = StateObject(wrappedValue: PagingViewModel<T>(pageComponent))
        self.columns = columns
        self.contentPadding = contentPadding
        self.key = key
        self.itemContent = itemContent
    }
    
    var body: some View {
        ScrollView {
            LazyVGrid(
                columns: columns
            ) {
                ForEach(itemsWithID(), id: \.id) { pair in
                    self.itemContent(pair.value)
                }
            }
            .padding(contentPadding)
        }
    }
    
    private func itemsWithID() -> [Identified<T>] {
        viewModel.pageItems.map { Identified(value: $0, id: key($0)) }
    }
    
    private struct Identified<V>: Identifiable {
        let value: V
        let id: AnyHashable
    }
}
