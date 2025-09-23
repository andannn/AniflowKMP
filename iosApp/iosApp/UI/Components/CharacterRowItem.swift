import SwiftUI
import Shared

struct CharacterRowItem: View {
    let characterWithVoiceActor: CharacterWithVoiceActor
    let userStaffLanguage: UserStaffNameLanguage
    var onStaffClick: (StaffModel) -> Void = { _ in }
    var onCharacterClick: (CharacterModel) -> Void = { _ in }
    var shape: some Shape = RoundedRectangle(cornerRadius: 12)
    var body: some View {
        ZStack {
            shape
                .fill(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
            HStack(spacing: 8) {
                // Character section (left)
                Button(action: { onCharacterClick(characterWithVoiceActor.character) }) {
                    HStack(spacing: 8) {
                        AsyncImage(url: URL(string: characterWithVoiceActor.character.image ?? "")) { phase in
                            switch phase {
                            case .empty:
                                Color.gray.opacity(0.2)
                            case .success(let img):
                                img.resizable().scaledToFill()
                            case .failure:
                                Color.gray.opacity(0.2)
                            @unknown default:
                                Color.gray.opacity(0.2)
                            }
                        }
                        .frame(width: 72, height: 90)
                        .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))

                        VStack(alignment: .leading, spacing: 4) {
                            let name = characterWithVoiceActor.character.name?.getNameString(staffName: userStaffLanguage) ?? ""
                            Text(name)
                                .font(.body)
                                .foregroundColor(.primary)
                                .lineLimit(2)
                                .truncationMode(.tail)
                            Spacer()
                            Text(characterWithVoiceActor.role?.label() ?? "")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .frame(height: 90)
                    }
                }
                .buttonStyle(.plain)
                Spacer(minLength: 8)
                // Voice Actor section (right)
                if let va = characterWithVoiceActor.voiceActor {
                    Button(action: { onStaffClick(va) }) {
                        HStack(spacing: 8) {
                            VStack(alignment: .trailing, spacing: 4) {
                                Text(va.name?.getNameString(staffName: userStaffLanguage) ?? "")
                                    .font(.body)
                                    .foregroundColor(.primary)
                                    .multilineTextAlignment(.trailing)
                                    .lineLimit(2)
                                    .truncationMode(.tail)
                                Spacer()
                                Text(characterWithVoiceActor.voiceActorLanguage.label())
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .multilineTextAlignment(.trailing)
                                    .lineLimit(2)
                                    .truncationMode(.tail)
                            }
                            .frame(height: 90)
                            AsyncImage(url: URL(string: va.image ?? "")) { phase in
                                switch phase {
                                case .empty:
                                    Color.gray.opacity(0.2)
                                case .success(let img):
                                    img.resizable().scaledToFill()
                                case .failure:
                                    Color.gray.opacity(0.2)
                                @unknown default:
                                    Color.gray.opacity(0.2)
                                }
                            }
                            .frame(width: 72, height: 90)
                            .clipShape(RoundedRectangle(cornerRadius: 8, style: .continuous))
                        }
                    }
                    .buttonStyle(.plain)
                } else {
                    Spacer(minLength: 0)
                }
            }
            .padding(.horizontal, 8)
        }
        .frame(height: 90)
    }
}
