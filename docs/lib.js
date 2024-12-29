// Importez des bibliothèques nécessaires si vous utilisez Node.js
// const fetch = require('node-fetch');

/**
 * Client YouTube pour extraire les informations vidéo.
 */
class YouTubeClient {
    constructor(clientType = ClientType.ANDROID) {
        this.clientType = clientType;
        this.innertubeKey = "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8";
        this.baseUrl = "https://www.youtube.com/youtubei/v1/player";
    }

    /**
     * Extraire l'ID de la vidéo à partir d'une URL YouTube.
     */
    extractVideoID(url) {
        const patterns = [
            /(?:v=|\/)([0-9A-Za-z_-]{11})/,
            /([0-9A-Za-z_-]{11})/
        ];
        for (const pattern of patterns) {
            const match = url.match(pattern);
            if (match && match[1]) {
                return match[1];
            }
        }
        return /^[0-9A-Za-z_-]{11}$/.test(url) ? url : null;
    }

    /**
     * Obtenir les formats vidéo à partir d'une URL YouTube.
     */
    async getVideoFormats(youtubeUrl) {
        try {
            const videoId = this.extractVideoID(youtubeUrl);
            if (!videoId) return null;

            const playerResponse = await this.getInnertubePlayerResponse(videoId);

            if (!playerResponse || playerResponse.playabilityStatus?.status !== "OK") {
                return null;
            }

            const streamingData = playerResponse.streamingData;
            const videoDetails = playerResponse.videoDetails;

            if (!streamingData || !videoDetails) return null;

            const rawFormats = [...streamingData.formats, ...streamingData.adaptiveFormats];
            const durationSeconds = parseInt(videoDetails.lengthSeconds || "0", 10);

            return {
                videoId: videoDetails.videoId || videoId,
                title: videoDetails.title || "Unknown title",
                author: videoDetails.author || "Unknown author",
                durationSeconds,
                formats: rawFormats
            };
        } catch (error) {
            console.error("Error fetching video formats:", error);
            return null;
        }
    }

    /**
     * Récupérer la réponse player via l'API Innertube.
     */
    async getInnertubePlayerResponse(videoId) {
        try {
            const url = `${this.baseUrl}?key=${this.innertubeKey}`;
            const requestBody = {
                videoId,
                context: {
                    client: {
                        clientName: this.clientType.clientName,
                        clientVersion: this.clientType.clientVersion,
                        userAgent: this.clientType.userAgent,
                        androidSdkVersion: this.clientType.androidSdkVersion,
                        deviceModel: this.clientType.deviceModel
                    }
                }
            };

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'User-Agent': this.clientType.userAgent,
                    'Origin': 'https://youtube.com',
                    'Sec-Fetch-Mode': 'navigate'
                },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) {
                throw new Error(`HTTP error: ${response.status}`);
            }

            const responseData = await response.json();
            return responseData;
        } catch (error) {
            console.error("Error fetching Innertube player response:", error);
            return null;
        }
    }
}

/**
 * Types de clients supportés.
 */
const ClientType = {
    ANDROID: {
        clientName: "ANDROID",
        clientVersion: "18.11.34",
        userAgent: "com.google.android.youtube/18.11.34 (Linux; U; Android 11) gzip",
        androidSdkVersion: 30
    },
    WEB: {
        clientName: "WEB",
        clientVersion: "2.20220801.00.00",
        userAgent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    },
    IOS: {
        clientName: "IOS",
        clientVersion: "19.45.4",
        userAgent: "com.google.ios.youtube/19.45.4 (iPhone16,2; U; CPU iOS 18_1_0 like Mac OS X;)",
        deviceModel: "iPhone16,2"
    }
};

// Exemple d'utilisation
(async () => {
    const client = new YouTubeClient(ClientType.WEB);
    const videoInfo = await client.getVideoFormats("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    console.log(videoInfo);
})();
