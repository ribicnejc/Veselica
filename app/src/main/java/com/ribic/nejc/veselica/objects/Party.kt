package com.ribic.nejc.veselica.objects

class Party {
    var date: String? = null
    var place: String? = null
    var href: String? = null
    var id: String? = null

    constructor() {}

    constructor(date: String, place: String, href: String, id: String) {
        this.date = date
        this.place = place
        this.href = href
        this.id = id
    }

    override fun toString(): String {
        return String.format("%s@%s@%s@%s", this.date, this.place, this.href, this.id)
    }
}
