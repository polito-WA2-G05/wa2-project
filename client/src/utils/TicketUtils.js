export class TicketStatus {
     static OPEN = "OPEN"
     static CANCELLED = "CANCELLED"
     static REOPENED = "REOPENED"
     static IN_PROGRESS = "IN_PROGRESS"
     static CLOSED = "CLOSED"
     static RESOLVED = "RESOLVED"

    static allCases = [
        { label: TicketStatus.OPEN, value: 0 },
        { label: TicketStatus.CANCELLED, value: 1 },
        { label: TicketStatus.REOPENED, value: 2 },
        { label: TicketStatus.IN_PROGRESS, value: 3 },
        { label: TicketStatus.CLOSED, value: 4 },
        { label: TicketStatus.RESOLVED, value: 5 }
    ]
}

export class PriorityLevel {
    static LOW = 0
    static MEDIUM = 1
    static HIGH = 2
    static URGENT = 3
}

export class Rating {
    static UNACCEPTABLE = 0;
    static BAD = 1;
    static FAIR = 2;
    static GOOD = 3;
    static EXCELLENT = 4;
}