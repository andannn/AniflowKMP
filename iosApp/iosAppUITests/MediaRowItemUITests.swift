import XCTest

final class MediaRowItemUITests: XCTestCase {

    func testMediaRowItemDisplaysNextAndUpcomingEpisodes() throws {
        let app = XCUIApplication()
        app.launchEnvironment["UITEST_MEDIA_ROW"] = "1"
        app.launch()

        let nextUpText = app.staticTexts["Next up: Episode 4"]
        XCTAssertTrue(nextUpText.waitForExistence(timeout: 5))

        let upcomingText = app.staticTexts["Episode 6 in 2 hours"]
        XCTAssertTrue(upcomingText.exists)

        let infoText = app.staticTexts["TV(Original) · 2024 · Summer · 24 Ep · Releasing"]
        XCTAssertTrue(infoText.exists)
    }
}
