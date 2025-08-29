import Shared
import SwiftUI

@MainActor
class PagingViewModel<T>: ObservableObject {
    private var pageComponent: PageComponent
    @Published public var pageItems: Array<T> = []
    @Published public var status: LoadingStatus = LoadingStatusIdle()
    
    private var dataTask:  Task<(), any Error>? = nil
    private var stateTask: Task<(), any Error>? = nil
    
    init(_ pageComponent: PageComponent) {
        self.pageComponent = pageComponent
        print("PagingViewModel init \(self)")
        
        dataTask = Task { [weak self] in
            guard let stream = self?.pageComponent.pageItems() else { return }
            for try await items in stream {
                self?.pageItems = items as! Array<T>
            }
        }
        
        stateTask = Task { [weak self] in
            guard let stream = self?.pageComponent.pageStaus() else { return }

            for try await status in stream {
                self?.status = status
            }
        }
    }
    
    func loadNext() {
        pageComponent.loadNextPage()
    }
    
    deinit {
        print("PagingViewModel deinit \(self)")
        pageComponent.dispose()
        dataTask?.cancel()
        stateTask?.cancel()
    }
}

struct VerticalGridPaging<T, ItemView>: View where ItemView : View {
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
                
                if viewModel.status is LoadingStatusLoading {
                    ProgressView()
                        .gridCellColumns(columns.count)
                        .frame(height: 48)
                        .frame(maxWidth: .infinity)
                }
                
                if viewModel.status is LoadingStatusIdle {
                    Spacer()
                        .frame(height: 48)
                        .frame(maxWidth: .infinity)
                        .gridCellColumns(columns.count)
                        .onAppear {
                            viewModel.loadNext()
                        }
                }
            }
            .padding(contentPadding)
        }
    }
    
    private func itemsWithID() -> [Identified<T>] {
        viewModel.pageItems.map { Identified(value: $0, id: key($0)) }
    }
}

struct VerticalListPaging<T, ItemView>: View where ItemView : View {
    @StateObject private var viewModel: PagingViewModel<T>
    
    var contentPadding: EdgeInsets
    let key: (T) -> AnyHashable
    let itemContent: (T) -> ItemView
    
    init(
        pageComponent: PageComponent,
        contentPadding: EdgeInsets = .init(),
        key: @escaping (T) -> AnyHashable,
        @ViewBuilder itemContent: @escaping (T) -> ItemView
    ) {
        _viewModel = StateObject(wrappedValue: PagingViewModel<T>(pageComponent))
        self.contentPadding = contentPadding
        self.key = key
        self.itemContent = itemContent
    }
    
    var body: some View {
        ScrollView {
            LazyVStack {
                ForEach(itemsWithID(), id: \.id) { pair in
                    self.itemContent(pair.value)
                }
                
                if viewModel.status is LoadingStatusLoading {
                    ProgressView()
                        .frame(height: 48)
                        .frame(maxWidth: .infinity)
                }
                
                if viewModel.status is LoadingStatusIdle {
                    Spacer()
                        .frame(height: 48)
                        .frame(maxWidth: .infinity)
                        .onAppear {
                            viewModel.loadNext()
                        }
                }
            }
            .padding(contentPadding)
        }
    }
    
    private func itemsWithID() -> [Identified<T>] {
        viewModel.pageItems.map { Identified(value: $0, id: key($0)) }
    }
}

private struct Identified<V>: Identifiable {
    let value: V
    let id: AnyHashable
}
