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
                        // replaced AsyncImage with CustomAsyncImage
                        CustomAsyncImage(url: characterWithVoiceActor.character.image)
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

                            // replaced AsyncImage with CustomAsyncImage
                            CustomAsyncImage(url: va.image)
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
