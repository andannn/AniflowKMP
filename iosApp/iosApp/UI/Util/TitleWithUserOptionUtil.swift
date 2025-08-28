import Shared

func userTitleStream(
    title: Title
) -> AsyncStream<String> {
    AsyncStream { continuation in
        let authRepository = KoinHelper.shared.authRepository()

        let task = Task {
            for try await opts in authRepository.getUserOptionsFlow() {
                continuation.yield(selectUserTitle(title, opts.titleLanguage))
            }
            continuation.finish()
        }
        // 取消时结束
        continuation.onTermination = { a in
            task.cancel()
        }
    }
}

private func selectUserTitle(_ t: Title, _ lang: UserTitleLanguage) -> String {
    switch lang {
    case .romaji:  return t.romaji ?? t.english ?? t.native ?? ""
    case .english: return t.english ?? t.romaji ?? t.native ?? ""
    case .native:  return t.native ?? t.romaji ?? t.english ?? ""
    default : fatalError()
    }
}
