//
//  StaffRowItem.swift
//  iosApp
//
//  Created by QIngnan Jianag on 2025/09/23.
//

import SwiftUI
import Shared

struct StaffRowItem: View {
    let staffWithRole: StaffWithRole
    let userStaffLanguage: UserStaffNameLanguage
    var onClick: () -> Void = {}
    var shape: some Shape = RoundedRectangle(cornerRadius: 12)
    var body: some View {
        ZStack {
            shape
                .fill(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
            HStack(spacing: 8) {
                Button(action: onClick) {
                    HStack(spacing: 8) {
                        AsyncImage(url: URL(string: staffWithRole.staff.image ?? "")) { phase in
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
                            let name = staffWithRole.staff.name?.getNameString(staffName: userStaffLanguage) ?? ""
                            Text(name)
                                .font(.body)
                                .foregroundColor(.primary)
                                .lineLimit(2)
                                .truncationMode(.tail)
                            Spacer()
                            Text(staffWithRole.role)
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        .frame(height: 90)
                    }
                }
                .buttonStyle(.plain)
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 8)
        }
        .frame(height: 90)
    }
}
