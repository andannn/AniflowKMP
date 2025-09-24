import Shared

extension SimpleDate {
    func toLocalDate() -> Date? {
        var components = DateComponents()
        components.year = Int(year)
        components.month = Int(month)
        components.day = day?.intValue
        
        let calendar = Calendar(identifier: .gregorian)
        return calendar.date(from: components)
    }
    
    func format() -> String {
        if let localDate = toLocalDate() {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            return formatter.string(from: localDate)
        } else {
            return String(format: "%04d-%02d", year, month)
        }
    }
}
