// SwiftUI PhotoPicker Helper for iOS
// Add this to your iOS project's Swift code (e.g., in iosApp/iosApp/)
// This provides the actual UIKit/PhotosUI integration

import SwiftUI
import Photos
import PhotosUI

/**
 * Delegate for PHPickerViewController to handle photo selection.
 * When user selects photos, this converts them to Photo objects
 * by extracting metadata (creation date, album name).
 *
 * Usage:
 * let pickerDelegate = PhotoPickerDelegate { photos in
 *     // Handle selected Photo objects
 * }
 * let config = PHPickerConfiguration(photoLibrary: .shared())
 * let picker = PHPickerViewController(configuration: config)
 * picker.delegate = pickerDelegate
 * viewController.present(picker, animated: true)
 */
class PhotoPickerDelegate: NSObject, PHPickerViewControllerDelegate {
    
    typealias PhotosCallback = ([Photo]) -> Void
    private let onPhotosSelected: PhotosCallback
    
    init(onPhotosSelected: @escaping PhotosCallback) {
        self.onPhotosSelected = onPhotosSelected
    }
    
    func picker(_ picker: PHPickerViewController, didFinishPicking results: [PHPickerResult]) {
        picker.dismiss(animated: true)
        
        var selectedAssets: [PHAsset] = []
        
        for result in results {
            // Get the local identifier from the picker result
            if let identifier = result.assetIdentifier {
                let fetchResults = PHAsset.fetchAssets(
                    withLocalIdentifiers: [identifier],
                    options: nil
                )
                if fetchResults.count > 0 {
                    if let asset = fetchResults.firstObject {
                        selectedAssets.append(asset)
                    }
                }
            }
        }
        
        // Convert PHAsset objects to Photo objects
        let photos = selectedAssets.compactMap { asset in
            photoFromAsset(asset)
        }
        
        onPhotosSelected(photos)
    }
    
    /**
     * Converts a PHAsset to a Photo object by extracting metadata.
     */
    private func photoFromAsset(_ asset: PHAsset) -> Photo {
        // Extract creation date
        var takenAt: String? = nil
        if let creationDate = asset.creationDate {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            takenAt = formatter.string(from: creationDate)
        }
        
        // Extract album/collection name
        var albumName: String? = nil
        let collections = PHAssetCollection.fetchAssetCollections(
            with: .album,
            subtype: .any,
            options: nil
        )
        collections.enumerateObjects { collection, _, stop in
            let fetchResults = PHAsset.fetchAssets(in: collection, options: nil)
            if fetchResults.contains(asset) {
                albumName = collection.localizedTitle
                stop.pointee = true
            }
        }
        
        // Create Photo object
        return Photo(
            id: asset.localIdentifier,
            uri: asset.localIdentifier,
            takenAt: takenAt,
            album: albumName,
            latitude: nil,
            longitude: nil,
            locationName: nil
        )
    }
}

/**
 * SwiftUI view modifier to present the photo picker.
 * Usage:
 * @State var selectedPhotos: [Photo] = []
 * MyView()
 *     .photoPickerSheet(isPresented: $showPhotoPicker) { photos in
 *         selectedPhotos = photos
 *     }
 */
extension View {
    func photoPickerSheet(
        isPresented: Binding<Bool>,
        onPhotosSelected: @escaping ([Photo]) -> Void
    ) -> some View {
        sheet(isPresented: isPresented) {
            PhotoPickerContainer(onPhotosSelected: onPhotosSelected)
        }
    }
}

/**
 * Container view that presents PHPickerViewController in SwiftUI.
 */
struct PhotoPickerContainer: UIViewControllerRepresentable {
    let onPhotosSelected: ([Photo]) -> Void
    @Environment(\.dismiss) var dismiss
    
    func makeUIViewController(context: Context) -> PHPickerViewController {
        var config = PHPickerConfiguration(photoLibrary: .shared())
        config.selectionLimit = 0 // Unlimited selection
        config.preferredAssetRepresentationMode = .current
        
        let picker = PHPickerViewController(configuration: config)
        picker.delegate = context.coordinator
        return picker
    }
    
    func updateUIViewController(_ uiViewController: PHPickerViewController, context: Context) {}
    
    func makeCoordinator() -> PhotoPickerDelegate {
        PhotoPickerDelegate { photos in
            onPhotosSelected(photos)
            dismiss()
        }
    }
}
