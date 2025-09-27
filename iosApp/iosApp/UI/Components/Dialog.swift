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
                .padding(.bottom, 12)
                .background(
                    RoundedRectangle(cornerRadius: 20, style: .continuous)
                        .fill(Color(.systemBackground))
                        .shadow(color: Color(.black).opacity(0.08), radius: 16, x: 0, y: 4)
                )
                .padding(.horizontal, 24)
                .frame(maxWidth: 360)
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
