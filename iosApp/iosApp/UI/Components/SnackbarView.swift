import SwiftUI

extension View {
    func snackbar(manager: SnackbarManager) -> some View {
        self.modifier(SnackbarModifier(manager: manager))
    }
}

enum SnackbarResult {
    case dismissed
    case actionPerformed
}

final class SnackbarData {
    let item: SnackbarItem
    let continuation: CheckedContinuation<SnackbarResult, Never>
    
    init(item: SnackbarItem, continuation: CheckedContinuation<SnackbarResult, Never>) {
        self.item = item
        self.continuation = continuation
    }
    
    func dismiss() {
        continuation.resume(returning: .dismissed)
    }

    func performAction() {
        continuation.resume(returning: .actionPerformed)
    }
}

@MainActor
final class SnackbarManager: ObservableObject {
    @Published var current: SnackbarData?

    @MainActor
    func show(_ message: String,
              actionTitle: String? = nil,
              duration: TimeInterval? = nil) async -> SnackbarResult {
        let result = await withTaskCancellationHandler {
            await withCheckedContinuation { (continuation: CheckedContinuation<SnackbarResult, Never>) in
                let item = SnackbarItem(message: message, actionLabel: actionTitle, duration: duration)
                let data = SnackbarData(item: item, continuation: continuation)
                self.current = data
            }
        } onCancel: {
            Task { @MainActor in
                self.current = nil
            }
        }
        
        self.current = nil

        return result
    }
    
    func dismiss(_ reason: SnackbarResult) {
        switch (reason) {
        case .dismissed:
            current?.dismiss()
        case .actionPerformed:
            current?.performAction()
        }
    }
}

private extension Animation {
    static let snackbarIn = Animation.spring(response: 0.35, dampingFraction: 0.8)
    static let snackbarOut = Animation.easeOut(duration: 0.25)
    static let snackbarSpring = Animation.spring(response: 0.4, dampingFraction: 0.9)
}

struct SnackbarItem: Equatable {
    var message: String
    var actionLabel: String? = nil
    var duration: TimeInterval? = nil
}

private struct SnackbarView: View {
    let item: SnackbarItem
    let scheme: ColorScheme
    let onAction: () -> Void
    let onDragEnd: () -> Void
    @Binding var offsetY: CGFloat
    
    var body: some View {
        HStack {
            Text(item.message)
                .foregroundColor(.white)
                .lineLimit(2)
            
            if let action = item.actionLabel {
                Spacer()
                Button(action) {
                    onAction()
                }
                .foregroundColor(.yellow)
                .bold()
            }
        }
        .padding()
        .background(.black.opacity(0.9))
        .cornerRadius(12)
        .offset(y: offsetY)
        .gesture(
            DragGesture().onChanged { value in
                if value.translation.height > 0 {
                    offsetY = value.translation.height
                }
            }.onEnded { value in
                if value.translation.height > 50 {
                    onDragEnd()
                } else {
                    withAnimation { offsetY = 0 }
                }
            }
        )
    }
}

private struct SnackbarModifier: ViewModifier {
    @ObservedObject var manager: SnackbarManager
    @State private var offsetY: CGFloat = 0
    @Environment(\.colorScheme) private var scheme
    
    func body(content: Content) -> some View {
        GeometryReader { proxy in
            let bottomInset = proxy.safeAreaInsets.bottom
            
            ZStack {
                content
                if let data = manager.current {
                    SnackbarView(item: data.item,
                                 scheme: scheme,
                                 onAction: { manager.dismiss(SnackbarResult.actionPerformed) },
                                 onDragEnd: { manager.dismiss(SnackbarResult.dismissed)},
                                 offsetY: $offsetY)
                    .padding(.horizontal, 16)
                    .padding(.bottom, max(bottomInset, 12))
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .zIndex(999)
                    .onAppear {
                        Task {
                            if let duration = data.item.duration {
                                try await Task.sleep(nanoseconds: UInt64(duration * 1_000_000_000))
                                manager.dismiss(SnackbarResult.dismissed)
                            }
                        }
                    }
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .bottom)
        }
    }
}

