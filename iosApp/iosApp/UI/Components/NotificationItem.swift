import SwiftUI
import Shared

struct NotificationItemView: View {
    let model: NotificationModel
    var onCoverImageClick: () -> Void = {}
    var onNotificationClick: () -> Void = {}

    private var relative: String {
        let now = Int64(Date().timeIntervalSince1970)
        let seconds = max(0, now - Int64(model.createdAt))
        let days = seconds / 86_400
        let hours = seconds / 3_600
        let minutes = seconds / 60
        if days > 0 { return "\(days)d" }
        if hours > 0 { return "\(hours)h" }
        if minutes > 0 { return "\(minutes)m" }
        return "0m"
    }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            // 右上角时间
            Text(relative)
                .font(.callout)
                .opacity(0.7)
                .padding(8)

            HStack(spacing: 0) {
                // 左侧封面卡片（宽 85）
                ZStack {
                    RoundedRectangle(cornerRadius: 12, style: .continuous)
                        .fill(Color(.secondarySystemBackground))
                    let cover = getCoverImageUrl(model: model)
                    CustomAsyncImage(url: cover)
                }
                .frame(width: 85, height: 85)
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                .contentShape(Rectangle())
                .onTapGesture { onCoverImageClick() }

                Spacer().frame(width: 8)

                VStack(alignment: .leading, spacing: 0) {
                    contentText
                        .font(.caption)
                        .lineLimit(4)
                        .multilineTextAlignment(.leading)
                }
                .frame(maxWidth: .infinity, minHeight: 85, alignment: .leading)

                Spacer().frame(width: 12)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 8)
        }
        .contentShape(Rectangle())
        .onTapGesture { onNotificationClick() }
    }

    @ViewBuilder
    private var contentText: some View {
        if let n = model as? AiringNotification {
            buildAiringText(n)
        } else if let n = model as? FollowNotification {
            buildFollowText(n)
        } else if let n = model as? ActivityNotification {
            buildActivityText(n)
        } else if let n = model as? MediaNotification {
            buildMediaText(n)
        } else if model is MediaDeletion {
            Text("MediaDeletionNotification")
        } else {
            Text("") // 未知类型
        }
    }
}


private func decodeContext3(_ json: String) -> [String] {
    // 期望 ["前缀","中缀","后缀"]
    if let data = json.data(using: .utf8),
       let arr = (try? JSONSerialization.jsonObject(with: data)) as? [Any] {
        return arr.map { "\($0)" } + Array(repeating: "", count: max(0, 3 - arr.count))
    }
    return ["", "", ""]
}

private func rememberUserTitle(_ title: Title) -> String {
    // 这里按你的实际策略返回（native/romaji/english）
    return title.native ?? title.romaji ?? title.english ?? ""
}

private func buildAiringText(_ n: AiringNotification) -> Text {
    let ctx = decodeContext3(n.context) // [0], [1], [2]
    // "${ctx[0]} $episode ${ctx[1]} <title> ${ctx[2]}"
    let prefix = Text("\(ctx[0])\(n.episode)\(ctx[1]) ")
    let title = Text(rememberUserTitle(n.media.title!)).foregroundColor(.primary)
    let suffix = Text("\(ctx[2])")
    return prefix + title + suffix
}

private func buildFollowText(_ n: FollowNotification) -> Text {
    Text(n.user.name!).foregroundColor(.primary) + Text(n.context)
}

private func buildActivityText(_ n: ActivityNotification) -> Text {
    Text(n.user.name!).foregroundColor(.primary) + Text(n.context)
}

private func buildMediaText(_ n: MediaNotification) -> Text {
    // "<title><context>"
    let title = Text(rememberUserTitle(n.media.title!)).foregroundColor(.primary)
    return title + Text(n.context)
}


private func getCoverImageUrl(model: NotificationModel) -> String {
    switch model {
    case let n as AiringNotification:   return n.media.coverImage ?? ""
    case let n as FollowNotification:   return n.user.avatar ?? ""
    case let n as ActivityNotification: return n.user.avatar ?? ""
    case let n as MediaNotification:    return n.media.coverImage ?? ""
    case _ as MediaDeletion:            return ""
    default:                            return ""
    }
}
