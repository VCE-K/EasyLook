package cn.vce.easylook.feature_ai.presentation.ai_collection

sealed class AiCollectionEvent {
    data class DeleteCollection(val id: String): AiCollectionEvent()

}
