import SwiftUI
import Shared

@MainActor
class DetailMediaViewModel: ObservableObject {
    let mediaId: String
    @Published public var uiState: DetailUiState = DetailUiState.companion.Empty
    
    private let dataProvider: DetailMediaUiDataProvider
    
    private var dataTask:  Task<(), any Error>? = nil
    private var sideEffectTask:  Task<(), any Error>? = nil
    
    init(mediaId: String) {
        self.mediaId = mediaId
        dataProvider = KoinHelper.shared.detailMediaUiDataProvider(mediaId: mediaId)
        
        dataTask = Task { [weak self] in
            guard let stream = self?.dataProvider.detailUiDataFlowAsyncSequence() else { return }
            
            for try await state in stream {
                self?.uiState = state
            }
        }
        sideEffectTask = Task { [weak self] in
            guard let stream = self?.dataProvider.detailUiSideEffectAsyncSequence(true) else { return }
            for try await _ in stream {
                //                self?.uiState = state
            }
        }
        
    }
    
    
    deinit {
        print("DetailMediaViewModel deinit")
        dataTask?.cancel()
    }
}

struct DetailMediaView: View {
    let mediaId: String
    @StateObject private var viewModel: DetailMediaViewModel
    
    init(mediaId: String) {
        self.mediaId = mediaId
        _viewModel = StateObject(wrappedValue: DetailMediaViewModel(mediaId: mediaId))
    }
    
    var body: some View {
        let uiState = viewModel.uiState
        DetailMediaContent(
            title: uiState.title,
            staffList: uiState.staffList,
            mediaModel: uiState.mediaModel,
            characterList: uiState.characters,
            relations: uiState.relations,
            studioList: uiState.studioList,
            userOptions: uiState.userOptions,
            mediaListItem: uiState.mediaListItem,
            authedUser: uiState.authedUser,
            isRefreshing: false
        )
    }
}


// MARK: - DetailMediaContent (SwiftUI)
public struct DetailMediaContent: View {
    // Inputs (mirror Compose signature as much as makes sense in SwiftUI)
    public let title: String
    public let staffList: [StaffWithRole]
    public let mediaModel: MediaModel?
    public let characterList: [CharacterWithVoiceActor]
    public let relations: [MediaModelWithRelationType]
    public let studioList: [StudioModel]
    public let userOptions: UserOptions
    public let mediaListItem: MediaListModel?
    public let authedUser: UserModel?
    public let isRefreshing: Bool
    
    // Events
    public var onPullRefresh: () -> Void = {}
    public var onChangeStatus: (MediaListStatus) -> Void = { _ in }
    public var onLoginClick: () -> Void = {}
    public var onToggleFavoriteClick: () -> Void = {}
    public var onTrackProgressClick: () -> Void = {}
    public var onRatingClick: () -> Void = {}
    public var onAddToListClick: () -> Void = {}
    public var onTrailerClick: (String) -> Void = { _ in }
    public var onRelationItemClick: (MediaModelWithRelationType) -> Void = { _ in }
    public var onExternalLinkClick: (ExternalLink) -> Void = { _ in }
    public var onStaffMoreClick: () -> Void = {}
    public var onCharacterMoreClick: () -> Void = {}
    public var onStaffClick: (StaffModel) -> Void = { _ in }
    public var onCharacterClick: (CharacterModel) -> Void = { _ in }
    public var onPop: () -> Void = {}
    
    @State private var showOverflowMenu = false
    
    public init(
        title: String,
        staffList: [StaffWithRole],
        mediaModel: MediaModel?,
        characterList: [CharacterWithVoiceActor],
        relations: [MediaModelWithRelationType],
        studioList: [StudioModel],
        userOptions: UserOptions,
        mediaListItem: MediaListModel?,
        authedUser: UserModel?,
        isRefreshing: Bool,
        onPullRefresh: @escaping () -> Void = {},
        onChangeStatus: @escaping (MediaListStatus) -> Void = { _ in },
        onLoginClick: @escaping () -> Void = {},
        onToggleFavoriteClick: @escaping () -> Void = {},
        onTrackProgressClick: @escaping () -> Void = {},
        onRatingClick: @escaping () -> Void = {},
        onAddToListClick: @escaping () -> Void = {},
        onTrailerClick: @escaping (String) -> Void = { _ in },
        onRelationItemClick: @escaping (MediaModelWithRelationType) -> Void = { _ in },
        onExternalLinkClick: @escaping (ExternalLink) -> Void = { _ in },
        onStaffMoreClick: @escaping () -> Void = {},
        onCharacterMoreClick: @escaping () -> Void = {},
        onStaffClick: @escaping (StaffModel) -> Void = { _ in },
        onCharacterClick: @escaping (CharacterModel) -> Void = { _ in },
        onPop: @escaping () -> Void = {}
    ) {
        self.title = title
        self.staffList = staffList
        self.mediaModel = mediaModel
        self.characterList = characterList
        self.relations = relations
        self.studioList = studioList
        self.userOptions = userOptions
        self.mediaListItem = mediaListItem
        self.authedUser = authedUser
        self.isRefreshing = isRefreshing
        self.onPullRefresh = onPullRefresh
        self.onChangeStatus = onChangeStatus
        self.onLoginClick = onLoginClick
        self.onToggleFavoriteClick = onToggleFavoriteClick
        self.onTrackProgressClick = onTrackProgressClick
        self.onRatingClick = onRatingClick
        self.onAddToListClick = onAddToListClick
        self.onTrailerClick = onTrailerClick
        self.onRelationItemClick = onRelationItemClick
        self.onExternalLinkClick = onExternalLinkClick
        self.onStaffMoreClick = onStaffMoreClick
        self.onCharacterMoreClick = onCharacterMoreClick
        self.onStaffClick = onStaffClick
        self.onCharacterClick = onCharacterClick
        self.onPop = onPop
    }
    
    public var body: some View {
        ScrollView {
            
            LazyVStack(alignment: .leading, spacing: 16) {
                bannerSection
                headerSection
                infoStringSection
                hashtagSection
                airingCountdownSection
                relationsSection
                aboutSection
                characterSection
                staffSection
                trailerSection
                studioSection
                externalLinksSection
                Spacer(minLength: 64)
            }
            .padding(.horizontal, 16)
            .background(Color(.systemBackground))
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            //                        .toolbar { topToolbar }
            //                        .safeAreaInset(edge: .bottom) { bottomBar }
            .refreshable { onPullRefresh() }
        }
    }
    
    // MARK: - Sections
    @ViewBuilder private var bannerSection: some View {
        if let url = mediaModel?.bannerImage {
            AsyncImage(url: URL(string: url)) { phase in
                switch phase {
                case .empty: Rectangle().fill(.ultraThinMaterial).frame(height: 110)
                case .success(let img): img.resizable().scaledToFill().frame(height: 110)
                case .failure: Color.secondary.opacity(0.15).frame(height: 110)
                @unknown default: EmptyView()
                }
            }
            .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
        } else {
            Divider().hidden()
        }
    }
    
    @ViewBuilder private var headerSection: some View {
        HStack(alignment: .top, spacing: 8) {
            if let cover = mediaModel?.coverImage {
                AsyncImage(url: URL(string: cover)) { phase in
                    switch phase {
                    case .empty: Rectangle().overlay(ProgressView())
                    case .success(let img): img.resizable().scaledToFill()
                    case .failure: Color.secondary.opacity(0.15)
                    @unknown default: Color.secondary.opacity(0.15)
                    }
                }
                .frame(width: 120, height: 180)
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            }
            
            VStack(alignment: .leading, spacing: 8) {
                InfoArea(mediaModel: mediaModel)
            }
            Spacer()
        }
    }
    
    @ViewBuilder private var infoStringSection: some View {
        if let text = mediaModel?.infoString(), !text.isEmpty {
            Text(text)
        }
    }
    
    @ViewBuilder private var hashtagSection: some View {
        if let tags = mediaModel?.hashtag, !tags.isEmpty {
            let columns = [GridItem(.adaptive(minimum: 80), spacing: 8)]
            LazyVGrid(columns: columns, alignment: .leading, spacing: 8) {
                ForEach(tags, id: \.self) { tag in
                    if tag.count > 1, let url = URL(string: "https://twitter.com/hashtag/\(String(tag.dropFirst()))?src=hashtag_click") {
                        Link(tag, destination: url)
                            .font(.subheadline)
                            .foregroundStyle(Color(red: 0.113, green: 0.631, blue: 0.949))
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(Color(.systemGray6))
                            .clipShape(Capsule())
                    } else {
                        Text(tag)
                            .font(.subheadline)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(Color(.systemGray6))
                            .clipShape(Capsule())
                    }
                }
            }
        }
    }
    
    @ViewBuilder private var airingCountdownSection: some View {
        if let ep = mediaModel?.nextAiringEpisode?.episode, let until = mediaModel?.releasingTimeString() {
            Text("Episode \(ep) in \(until)")
                .font(.subheadline)
                .foregroundStyle(.primary)
        }
    }
    
    @ViewBuilder private var relationsSection: some View {
        if !relations.isEmpty {
            SectionHeader(title: "Relations")
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(Array(relations.enumerated()), id: \.offset) { i, r in
                        MediaRelationItem(userTitleLanguage: userOptions.titleLanguage, mediaRelation: relations[i])
                    }
                }
                .padding(.vertical, 4)
            }
            .frame(height: 128)
        }
    }
    
    @ViewBuilder private var aboutSection: some View {
        if let html = mediaModel?.description_, !html.isEmpty {
            SectionHeader(title: "About")
            Text(stripHTML(html)).font(.body)
        }
    }
    
        @ViewBuilder private var characterSection: some View {
            if !characterList.isEmpty {
                SectionHeader(title: "Character", showMore: true, onMore: onCharacterMoreClick)
                VStack(spacing: 0) {
                    ForEach(Array(characterList.enumerated()), id: \.offset) { index, item in
                        CharacterRowItem(
                            characterWithVoiceActor: item,
                            userStaffLanguage: userOptions.staffNameLanguage,
                            onStaffClick: onStaffClick,
                            onCharacterClick: onCharacterClick
                        )
                        .padding(.vertical, 4)
                        if index < characterList.count - 1 {
                            Divider().padding(.leading, 80)
                        }
                    }
                }
                .background(Color(.systemBackground))
                .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
                .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
            }
        }

    @ViewBuilder private var staffSection: some View {
        if !staffList.isEmpty {
            SectionHeader(title: "Staff", showMore: true, onMore: onStaffMoreClick)
            VStack(spacing: 0) {
                ForEach(Array(staffList.enumerated()), id: \.offset) { index, item in
                    StaffRowItem(
                        staffWithRole: item,
                        userStaffLanguage: userOptions.staffNameLanguage,
                        onClick: { onStaffClick(item.staff) }
                    )
                    .padding(.vertical, 4)
                    if index < staffList.count - 1 {
                        Divider().padding(.leading, 80)
                    }
                }
            }
            .background(Color(.systemBackground))
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))
            .shadow(color: Color.black.opacity(0.04), radius: 2, y: 1)
        }
    }
    
    @ViewBuilder  var trailerSection: some View {
        if let trailer = mediaModel?.trailer {
            SectionHeader(title: "Trailer")
            Button {
                //                    if let siteUrl = trailer.siteUrl { onTrailerClick(siteUrl) }
            } label: {
                AsyncImage(url: URL(string: trailer.thumbnail ?? "")) { phase in
                    switch phase {
                    case .empty: Rectangle().fill(.ultraThinMaterial).frame(height: 180)
                    case .success(let img): img.resizable().scaledToFill().frame(height: 180)
                    case .failure: Color.secondary.opacity(0.15).frame(height: 180)
                    @unknown default: EmptyView()
                    }
                }
                .clipShape(RoundedRectangle(cornerRadius: 16, style: .continuous))
            }
            .buttonStyle(.plain)
        }
    }
    
    @ViewBuilder  var studioSection: some View {
        if !studioList.isEmpty {
            SectionHeader(title: "Studio")
            let columns = [GridItem(.adaptive(minimum: 80), spacing: 8)]
            LazyVGrid(columns: columns, alignment: .leading, spacing: 8) {
                ForEach(studioList, id: \.name) { studio in
                    Button(action: {}) {
                        Text(studio.name)
                    }
                    .buttonStyle(.bordered)
                }
            }
        }
    }
    
    @ViewBuilder  var externalLinksSection: some View {
        if let links = mediaModel?.externalLinks, !links.isEmpty {
            SectionHeader(title: "External & Streaming links")
            let columns = [GridItem(.adaptive(minimum: 120), spacing: 8)]
            LazyVGrid(columns: columns, alignment: .leading, spacing: 8) {
                ForEach(links, id: \.site) { link in
                    Button(action: { onExternalLinkClick(link) }) {
                        HStack(spacing: 6) {
                            if let icon = link.icon, let url = URL(string: icon) {
                                AsyncImage(url: url) { phase in
                                    switch phase {
                                    case .empty: Color.clear.frame(width: 20, height: 20)
                                    case .success(let img): img.resizable().scaledToFit().frame(width: 20, height: 20).clipShape(RoundedRectangle(cornerRadius: 4))
                                    case .failure: Color.clear.frame(width: 20, height: 20)
                                    @unknown default: Color.clear.frame(width: 20, height: 20)
                                    }
                                }
                            }
                            Text(link.site)
                        }
                    }
                    .buttonStyle(.bordered)
                }
            }
        }
    }

    //    @ViewBuilder private var bottomBar: some View {
    //        HStack(spacing: 12) {
    //            if let user = authedUser { // logged in
    //                if let current = mediaListItem { // show status menu
    //                    Menu {
    //                        let items = mediaModel?.status ?? MediaListStatus.allCases
    //                        ForEach(items) { s in
    //                            Button(s.rawValue) { onChangeStatus(s) }
    //                        }
    //                    } label: {
    //                        Label(current.status.rawValue, systemImage: "list.bullet")
    //                            .padding(.horizontal, 12)
    //                    }
    //                    .buttonStyle(.borderedProminent)
    //                } else {
    //                    Button { onAddToListClick() } label: { Label("Add to List", systemImage: "plus") }
    //                        .buttonStyle(.bordered)
    //                }
    //            } else { // not logged in
    //                Button { onLoginClick() } label: { Label("Login", systemImage: "person") }
    //                    .buttonStyle(.bordered)
    //            }
    //
    //            Spacer()
    //        }
    //        .padding(.horizontal, 16)
    //        .padding(.top, 8)
    //        .padding(.bottom, 12)
    //        .background(.ultraThinMaterial)
    //    }
    //
}

func stripHTML(_ html: String) -> String {
    html.replacingOccurrences(of: "<[^>]+>", with: " ", options: .regularExpression)
        .replacingOccurrences(of: "&nbsp;", with: " ")
        .replacingOccurrences(of: "&amp;", with: "&")
        .replacingOccurrences(of: "&lt;", with: "<")
        .replacingOccurrences(of: "&gt;", with: ">")
        .trimmingCharacters(in: .whitespacesAndNewlines)
}
