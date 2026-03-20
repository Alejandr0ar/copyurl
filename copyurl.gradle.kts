description = "copy url."

zapAddOn {
    addOnName.set("copy url")

    manifest {
        author.set("ZAP Dev Team")
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}
