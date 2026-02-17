import torch
from torchvision import datasets, models, transforms
import torch.nn as nn
import torch.optim as optim
import os

print("Starting training script...")

DATASET_DIR = "material_dataset"
EPOCHS = 5
BATCH_SIZE = 32
MODEL_OUT = "efficientnet_material.pth"

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
print("Using device:", device)

# Transforms
transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225]
    )
])

# Dataset paths
train_path = os.path.join(DATASET_DIR, "train")
val_path = os.path.join(DATASET_DIR, "val")

print("Loading dataset from:", train_path)

train_ds = datasets.ImageFolder(train_path, transform=transform)
val_ds = datasets.ImageFolder(val_path, transform=transform)

print("Classes found:", train_ds.classes)
print("Training images:", len(train_ds))
print("Validation images:", len(val_ds))

train_loader = torch.utils.data.DataLoader(
    train_ds, batch_size=BATCH_SIZE, shuffle=True
)

# Model
model = models.efficientnet_b0(pretrained=True)
model.classifier[1] = nn.Linear(
    model.classifier[1].in_features,
    len(train_ds.classes)
)
model.to(device)

criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(model.parameters(), lr=0.0003)

# Training loop
for epoch in range(EPOCHS):
    model.train()
    total_loss = 0

    for images, labels in train_loader:
        images, labels = images.to(device), labels.to(device)

        optimizer.zero_grad()
        outputs = model(images)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()

        total_loss += loss.item()

    print(f"Epoch {epoch+1}/{EPOCHS} - Loss: {total_loss:.4f}")

# Save model
torch.save(model.state_dict(), MODEL_OUT)
print("Training complete. Model saved as:", MODEL_OUT)
