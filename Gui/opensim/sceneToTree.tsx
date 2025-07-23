import * as THREE from 'three';

export function convertSceneToTree(scene: THREE.Scene | null, camera: THREE.Camera | null) {
  const traverse = (obj: any): any | null => {
    let canEdit = true;
    let id = obj.id;
    let title = obj.name
    let uuid = obj.uuid

    let isModel = false;
    if (obj.name === "Scene") {
      title = "Model";
      isModel = true;
      canEdit = false;
    }

    let isGroup = false;
    if (obj.type === "Group") {
      isGroup = true;
      canEdit = false;
    }
    console.log(obj.type)
    let isLight = false;
    if (obj.type.includes("Light"))
      isLight = true;

    let isSkySphere = false;
    if (obj.name.includes("SkySphere"))
      isSkySphere = true;

    let isFloor = false;
    if (obj.name.includes("Floor"))
      isFloor = true;

    let isAxes = false;
    if (obj.type.includes("Axes"))
      isAxes = true;

    let isCamera = false;
    if (obj.name.includes("Camera")) {
      isCamera = true;
      canEdit = false;
    }

    let children = [];
    if (!obj.type.includes("TransformControls") && !obj.type.includes("Helper") && obj.type !== "Object3D") {

      if ((obj.type === "Group" && obj.children.length > 0) || (obj.type === "Group" && obj.name === "Cameras")|| (obj.type === "Group" && obj.name === "Illumination")) {
        if (title !== "Model" && obj.children) {
          children = (obj.children || [])
            .map(traverse)
            .filter((child: any): child is NonNullable<typeof child> => child !== null);
        }

        if (obj.type === "Group" && obj.name === "Illumination") {
          // Append "+" node
          children.push({
            title: "",
            subtitle: "",
            visible: true,
            object3D: null,
            isGroup: false,
            isLight: false,
            isSkySphere: false,
            isFloor: false,
            isAxes: false,
            isModel: false,
            isCamera: false,
            canEdit: false,
            isAddCameraButton: false,
            isAddLightButton: true,
            id: "add-light-node",
            type: "AddButton",
            children: [],
          });
        }

        // Add camera as child if this is the "Cameras" group
        if (obj.type === "Group" && obj.name === "Cameras" && camera) {
          if (obj.children) {
            const cameraNode = {
              title: camera.name || "Default Camera",
              subtitle: camera.type,
              visible: camera.visible,
              object3D: camera,
              isGroup: false,
              isLight: false,
              isSkySphere: false,
              isFloor: false,
              isAxes: false,
              isModel: false,
              canEdit: false,
              isCamera: true,
              id: "default-camera",
              type: obj.type,
              children: []
            };
            children.unshift(cameraNode);

            // Append "+" node
            children.push({
              title: "",
              subtitle: "",
              visible: true,
              object3D: null,
              isGroup: false,
              isLight: false,
              isSkySphere: false,
              isFloor: false,
              isAxes: false,
              isModel: false,
              isCamera: false,
              canEdit: false,
              isAddCameraButton: true,
              isAddLightButton: false,
              id: "add-camera-node",
              type: "AddButton",
              children: [],
            });
          }
        }
      }


      return {
        title: title,
        subtitle: obj.type,
        visible: obj.visible,
        object3D: obj,
        isGroup: isGroup,
        isLight: isLight,
        isSkySphere: isSkySphere,
        isFloor: isFloor,
        isAxes: isAxes,
        isModel: isModel,
        canEdit: canEdit,
        isCamera: isCamera,
        id: id,
        uuid: uuid,
        type: obj.type,
        children: children
      };
    }
    return null
  };

  if (scene != null)
    return scene.children
      .map(traverse)
      .filter((child: any)=> child !== null);
  else
    return [];
}