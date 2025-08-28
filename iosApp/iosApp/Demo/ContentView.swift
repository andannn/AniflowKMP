import SwiftUI
import Combine

// MARK: - ViewModel
@MainActor
final class CounterViewModel: ObservableObject {
    @Published var count: Int = 0
    let sourceID: Int

    init(sourceID: Int) {
        self.sourceID = sourceID
        print("🟢 init VM sourceID=\(sourceID)  \(Unmanaged.passUnretained(self).toOpaque())")
    }

    deinit {
        print("🔴 deinit VM sourceID=\(sourceID)  \(Unmanaged.passUnretained(self).toOpaque())")
    }
}

struct ContentView: View {
    @State private var sourceID: Int = 1
    @State private var mode: Mode = .stateObject
    @State private var forceRecreate = false // 控制 .id 强制重建

    enum Mode: String, CaseIterable, Identifiable {
        case stateObject = "@StateObject 子视图内创建"
        case observedObject = "@ObservedObject 外部传入"
        var id: String { rawValue }
    }

    var body: some View {
        VStack(spacing: 16) {
            // 切换数据源
            HStack {
                Button("切换 sourceID") {
                    sourceID = (sourceID % 3) + 1
                }
                Text("当前 sourceID = \(sourceID)")
            }

            // 切换模式
            Picker("模式", selection: $mode) {
                ForEach(Mode.allCases) { m in
                    Text(m.rawValue).tag(m)
                }
            }
            .pickerStyle(.segmented)

            // 是否强制重建（仅对 @StateObject 的演示有意义）
            Toggle("对 @StateObject 使用 .id(sourceID) 强制重建", isOn: $forceRecreate)

            Divider()

            Group {
                switch mode {
                case .stateObject:
                    let view = Child_StateObject(sourceID: sourceID)
                    if forceRecreate {
                        view.id(sourceID) // 身份变化 ⇒ 触发 @StateObject 重建
                    } else {
                        view // 身份不变 ⇒ 保留原来的 VM
                    }

                case .observedObject:
                    // 注意：Observed 由父级创建并传入
                    Child_ObservedObject(viewModel: CounterViewModel(sourceID: sourceID))
                }
            }
            .padding()
            .border(.secondary)

            Spacer()
        }
        .padding()
    }
}

// MARK: - Child using @StateObject (在子视图内部创建并持有 VM)
struct Child_StateObject: View {
    let sourceID: Int
    @StateObject private var vm: CounterViewModel

    init(sourceID: Int) {
        self.sourceID = sourceID
        _vm = StateObject(wrappedValue: CounterViewModel(sourceID: sourceID))
    }

    var body: some View {
        VStack(spacing: 8) {
            Text("Child: @StateObject")
                .font(.headline)

            Text("构造入参 sourceID: \(sourceID)")
            Text("VM 持有的 sourceID: \(vm.sourceID)")
            Text("count: \(vm.count)")

            HStack {
                Button("＋1") { vm.count += 1 }
                Button("清零") { vm.count = 0 }
            }
        }
        .onAppear { print("👀 Child_StateObject appear") }
    }
}

// MARK: - Child using @ObservedObject (由父视图创建后传入)
struct Child_ObservedObject: View {
    @ObservedObject var viewModel: CounterViewModel

    var body: some View {
        VStack(spacing: 8) {
            Text("Child: @ObservedObject")
                .font(.headline)

            Text("VM 持有的 sourceID: \(viewModel.sourceID)")
            Text("count: \(viewModel.count)")

            HStack {
                Button("＋1") { viewModel.count += 1 }
                Button("清零") { viewModel.count = 0 }
            }
        }
        .onAppear { print("👀 Child_ObservedObject appear") }
    }
}
