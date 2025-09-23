//
//  InfoItemHorizon.swift
//  iosApp
//
//  Created by QIngnan Jianag on 2025/09/23.
//

import SwiftUI

/// SwiftUI 风格优化的 InfoItemHorizon 组件
struct InfoItemHorizon: View {
    /// 支持 SF Symbols 或 Asset 图片
    let iconName: String
    let isSystemImage: Bool
    let contentText: String
    var iconColor: Color = .accentColor
    var numberColor: Color = .accentColor
    var font: Font = .footnote
    var specialFont: Font = .footnote.bold()
    var spacing: CGFloat = 8

    var body: some View {
        HStack(spacing: spacing) {
            if isSystemImage {
                Image(systemName: iconName)
                    .foregroundColor(iconColor)
            } else {
                Image(iconName)
                    .renderingMode(.template)
                    .foregroundColor(iconColor)
            }
            formattedText
        }
    }

    /// 构建高亮数字/特殊字符的富文本
    private var formattedText: some View {
        let attributed = buildAttributedString(
            contentText: contentText,
            numberColor: numberColor,
            font: font,
            specialFont: specialFont
        )
        return Text(attributed)
    }

    /// 构建 AttributedString，数字/#/% 用特殊字体和颜色
    private func buildAttributedString(contentText: String, numberColor: Color, font: Font, specialFont: Font) -> AttributedString {
        var result = AttributedString("")
        for char in contentText {
            var attr = AttributedString(String(char))
            if char.isNumber || char == "#" || char == "%" {
                attr.foregroundColor = numberColor
                attr.font = specialFont
            } else {
                attr.font = font
            }
            result += attr
        }
        return result
    }
}

#if DEBUG
struct InfoItemHorizon_Previews: PreviewProvider {
    static var previews: some View {
        VStack(spacing: 20) {
            InfoItemHorizon(
                iconName: "star.fill",
                isSystemImage: true,
                contentText: "评分 9.5/10 #Top1",
                iconColor: .yellow,
                numberColor: .red,
                font: .subheadline,
                specialFont: .title3.bold()
            )
            InfoItemHorizon(
                iconName: "DemoIcon",
                isSystemImage: false,
                contentText: "进度 12/24 %",
                iconColor: .blue,
                numberColor: .green,
                font: .body,
                specialFont: .title2.bold()
            )
        }
        .padding()
        .previewLayout(.sizeThatFits)
    }
}
#endif
