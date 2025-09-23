import SwiftUI

struct HTMLText: View {
    let html: String
    var body: some View {
        if let attr = try? AttributedString(html: html) {
            Text(attr)
                .lineSpacing(4)
                .tint(.blue)
        } else {
            // 解析失败时的兜底：去标签显示纯文本
            Text(html.replacingOccurrences(of: "<[^>]+>", with: " ", options: .regularExpression))
        }
    }
}

extension AttributedString {
    init(html: String) throws {
        let data = Data(html.utf8)
        let options: [NSAttributedString.DocumentReadingOptionKey: Any] = [
            .documentType: NSAttributedString.DocumentType.html,
            .characterEncoding: String.Encoding.utf8.rawValue
        ]
        let ns = try NSMutableAttributedString(data: data, options: options, documentAttributes: nil)

        // 统一设定基础字体/颜色（避免 HTML 自带的 Times/颜色影响动态字体）
        let baseFont = UIFont.preferredFont(forTextStyle: .body)
        ns.enumerateAttribute(.font, in: NSRange(location: 0, length: ns.length)) { value, range, _ in
            let original = (value as? UIFont) ?? baseFont
            // 尽量保持原字号权重，仅替换为系统字体族，支持动态字体
            let new = UIFontMetrics(forTextStyle: .body)
                .scaledFont(for: UIFont.systemFont(ofSize: original.pointSize, weight: original.weight))
            ns.addAttribute(.font, value: new, range: range)
        }
        ns.addAttribute(.foregroundColor, value: UIColor.label, range: NSRange(location: 0, length: ns.length))

        self = AttributedString(ns)
    }
}

private extension UIFont {
    var weight: UIFont.Weight {
        let traits = fontDescriptor.object(forKey: .traits) as? [UIFontDescriptor.TraitKey: Any]
        let weightNumber = traits?[.weight] as? NSNumber
        return UIFont.Weight(rawValue: CGFloat(weightNumber?.doubleValue ?? 0))
    }
}
