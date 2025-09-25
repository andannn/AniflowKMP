import SwiftUI

struct DialogModifier<DialogContent: View>: ViewModifier {
    @Binding var isPresented: Bool
    let dialogContent: () -> DialogContent

    func body(content: Content) -> some View {
        ZStack {
            content
            if isPresented {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture { isPresented = false }

                VStack(spacing: 0) {
                    Capsule()
                        .fill(Color.secondary.opacity(0.3))
                        .frame(width: 40, height: 5)
                        .padding(.top, 8)
                        .padding(.bottom, 12)
                    dialogContent()
                        .padding(.horizontal, 20)
                        .padding(.bottom, 24)
                }
                .background(
                    .regularMaterial,
                    in: RoundedRectangle(cornerRadius: 24, style: .continuous)
                )
                .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
                .frame(maxWidth: 340)
                .transition(.scale)
                .animation(.easeOut, value: isPresented)
            }
        }
    }
}

extension View {
    func customDialog<DialogContent: View>(
        isPresented: Binding<Bool>,
        @ViewBuilder content: @escaping () -> DialogContent
    ) -> some View {
        self.modifier(DialogModifier(isPresented: isPresented, dialogContent: content))
    }
}
