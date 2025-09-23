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
                    CustomAsyncImage(url: staffWithRole.staff.image)
                        .frame(width: 64, height: 96)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
          
                    
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
                    
                    Spacer(minLength: 0)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 8)
        }
        .frame(height: 90)
    }
}
