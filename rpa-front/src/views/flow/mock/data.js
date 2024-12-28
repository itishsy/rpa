export const mockStateGraph = 
{
    "cells": [
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#A2B1C3",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "9cf06d84-7aab-4e5c-bbed-84a29ae7e153",
            "zIndex": 0,
            "source": {
                "cell": "cfa2cc13-81a1-4725-a124-adc9791bf10a",
                "port": "5e72cb79-a2db-4992-a878-a3d9e123bf07"
            },
            "target": {
                "cell": "022e79d7-8554-4188-b500-bdaefe653e67",
                "port": "1b829159-aa8f-44da-bb1f-07f8fcb6e259"
            }
        },
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#A2B1C3",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "c5b3b6df-bf46-40ee-a1d9-fc5b4419c51f",
            "zIndex": 0,
            "source": {
                "cell": "022e79d7-8554-4188-b500-bdaefe653e67",
                "port": "70cdd910-30b6-47fc-b94d-b5d3e479b654"
            },
            "target": {
                "cell": "d97ca6ae-7331-40e3-90e6-2d30a814aa7d",
                "port": "e19a2bb8-6c4f-4cb8-9eba-edaadd1fedb5"
            }
        },
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#67C23A",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "e6cc0665-dfee-4706-891b-47d2e11d51f5",
            "zIndex": 0,
            "source": {
                "cell": "d97ca6ae-7331-40e3-90e6-2d30a814aa7d",
                "port": "fb9077bf-75be-4446-bf01-145c74b22b5d"
            },
            "target": {
                "cell": "686ff216-e00d-4afc-9971-34664254406f",
                "port": "b83a674a-5b94-401e-a21c-04f442c7ea93"
            },
            "data": {
                "desc": "",
                "label": "是",
                "nodeState": "1"
            },
            "labels": [
                "是"
            ]
        },
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#F56C6C",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "6144aa26-2d50-458d-8a3d-0edeb32d4839",
            "zIndex": 0,
            "source": {
                "cell": "d97ca6ae-7331-40e3-90e6-2d30a814aa7d",
                "port": "fb9077bf-75be-4446-bf01-145c74b22b5d"
            },
            "target": {
                "cell": "78b1f54e-8f9c-412f-b2ce-83f9b1844648",
                "port": "1b829159-aa8f-44da-bb1f-07f8fcb6e259"
            },
            "data": {
                "desc": "",
                "label": "否",
                "nodeState": "2"
            },
            "labels": [
                "否"
            ]
        },
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#67C23A",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "f94fa0cc-c54c-48d3-8ef1-26ed017c7473",
            "zIndex": 0,
            "source": {
                "cell": "686ff216-e00d-4afc-9971-34664254406f",
                "port": "6addaea2-7540-4b19-8d64-d7926a8ba04a"
            },
            "target": {
                "cell": "7bb8fbed-83c1-43e8-adf9-86e7125857f2",
                "port": "91b0e304-7767-4d90-92a1-ee5ebb30a61c"
            },
            "data": {
                "desc": "",
                "label": "任务完成",
                "nodeState": "1"
            },
            "labels": [
                "任务完成"
            ]
        },
        {
            "shape": "edge",
            "attrs": {
                "line": {
                    "stroke": "#A2B1C3",
                    "targetMarker": {
                        "name": "block",
                        "width": 12,
                        "height": 8
                    }
                }
            },
            "id": "d44236d1-fa2b-42ef-92d6-d1597e1ea8bc",
            "zIndex": 0,
            "source": {
                "cell": "78b1f54e-8f9c-412f-b2ce-83f9b1844648",
                "port": "70cdd910-30b6-47fc-b94d-b5d3e479b654"
            },
            "target": {
                "cell": "7bb8fbed-83c1-43e8-adf9-86e7125857f2",
                "port": "91b0e304-7767-4d90-92a1-ee5ebb30a61c"
            },
            "tools": {
                "items": [],
                "name": null
            }
        },
        {
            "position": {
                "x": 220,
                "y": -40
            },
            "size": {
                "width": 86,
                "height": 36
            },
            "attrs": {
                "text": {
                    "text": "开始"
                },
                "body": {
                    "rx": 20,
                    "ry": 26
                }
            },
            "visible": true,
            "shape": "custom-rect",
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "7c99a818-a82f-4bdb-8ebe-a0a70a9b1616"
                    },
                    {
                        "group": "right",
                        "id": "d188d314-1208-4777-9a27-0347b1b20afd"
                    },
                    {
                        "group": "bottom",
                        "id": "5e72cb79-a2db-4992-a878-a3d9e123bf07"
                    },
                    {
                        "group": "left",
                        "id": "1a434407-1aee-4358-97dc-a4a1741f8073"
                    }
                ]
            },
            "id": "cfa2cc13-81a1-4725-a124-adc9791bf10a",
            "zIndex": 1
        },
        {
            "position": {
                "x": 220,
                "y": 54
            },
            "size": {
                "width": 86,
                "height": 36
            },
            "attrs": {
                "text": {
                    "text": "过程"
                },
                "body": {
                    "stroke": "#67C23A",
                    "fill": "#E1F3D8"
                },
                "label": {
                    "text": "政务审批"
                }
            },
            "visible": true,
            "shape": "custom-rect",
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "1b829159-aa8f-44da-bb1f-07f8fcb6e259"
                    },
                    {
                        "group": "right",
                        "id": "b55af887-75a7-4d0e-b2a3-44b2fc98d8ea"
                    },
                    {
                        "group": "bottom",
                        "id": "70cdd910-30b6-47fc-b94d-b5d3e479b654"
                    },
                    {
                        "group": "left",
                        "id": "8d46109b-6a0e-4e86-8519-0859896cfa2d"
                    }
                ]
            },
            "id": "022e79d7-8554-4188-b500-bdaefe653e67",
            "zIndex": 2,
            "data": {
                "desc": "",
                "label": "政务审批",
                "nodeState": "1"
            }
        },
        {
            "position": {
                "x": 213,
                "y": 138
            },
            "size": {
                "width": 100,
                "height": 50
            },
            "attrs": {
                "text": {
                    "text": "判断节点"
                },
                "body": {
                    "stroke": "#67C23A",
                    "fill": "#E1F3D8",
                    "refPoints": "0,10 10,0 20,10 10,20"
                },
                "label": {
                    "text": "会务属于上级？"
                }
            },
            "visible": true,
            "shape": "custom-polygon",
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "e19a2bb8-6c4f-4cb8-9eba-edaadd1fedb5"
                    },
                    {
                        "group": "bottom",
                        "id": "fb9077bf-75be-4446-bf01-145c74b22b5d"
                    }
                ]
            },
            "id": "d97ca6ae-7331-40e3-90e6-2d30a814aa7d",
            "zIndex": 3,
            "data": {
                "desc": "判断条件",
                "label": "会务属于上级？",
                "nodeState": "1"
            }
        },
        {
            "position": {
                "x": 60,
                "y": 246
            },
            "size": {
                "width": 150,
                "height": 40
            },
            "view": "vue-shape-view",
            "shape": "vue-shape",
            "id": "686ff216-e00d-4afc-9971-34664254406f",
            "component": "extra-node",
            "data": {
                "num": 0,
                "label": "自定义操作",
                "desc": "描述",
                "nodeState": "1"
            },
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "b83a674a-5b94-401e-a21c-04f442c7ea93"
                    },
                    {
                        "group": "right",
                        "id": "bc4b85c4-0727-4600-b862-e13526fc8657"
                    },
                    {
                        "group": "bottom",
                        "id": "6addaea2-7540-4b19-8d64-d7926a8ba04a"
                    },
                    {
                        "group": "left",
                        "id": "f140d194-bce2-47d2-a20b-6e1b03432fc4"
                    }
                ]
            },
            "zIndex": 4
        },
        {
            "position": {
                "x": 328,
                "y": 248
            },
            "size": {
                "width": 86,
                "height": 36
            },
            "attrs": {
                "text": {
                    "text": "过程"
                },
                "body": {
                    "stroke": "#F56C6C",
                    "fill": "#FDE2E2"
                },
                "label": {
                    "text": "走从属流程"
                }
            },
            "visible": true,
            "shape": "custom-rect",
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "1b829159-aa8f-44da-bb1f-07f8fcb6e259"
                    },
                    {
                        "group": "right",
                        "id": "b55af887-75a7-4d0e-b2a3-44b2fc98d8ea"
                    },
                    {
                        "group": "bottom",
                        "id": "70cdd910-30b6-47fc-b94d-b5d3e479b654"
                    },
                    {
                        "group": "left",
                        "id": "8d46109b-6a0e-4e86-8519-0859896cfa2d"
                    }
                ]
            },
            "id": "78b1f54e-8f9c-412f-b2ce-83f9b1844648",
            "zIndex": 5,
            "data": {
                "desc": "",
                "label": "走从属流程",
                "nodeState": "2"
            }
        },
        {
            "position": {
                "x": 220,
                "y": 339
            },
            "size": {
                "width": 86,
                "height": 36
            },
            "attrs": {
                "text": {
                    "text": "结束"
                },
                "body": {
                    "rx": 20,
                    "ry": 26
                }
            },
            "visible": true,
            "shape": "custom-rect",
            "ports": {
                "groups": {
                    "top": {
                        "position": "top",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "right": {
                        "position": "right",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "bottom": {
                        "position": "bottom",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    },
                    "left": {
                        "position": "left",
                        "attrs": {
                            "circle": {
                                "r": 4,
                                "magnet": true,
                                "stroke": "#5F95FF",
                                "strokeWidth": 1,
                                "fill": "#fff",
                                "style": {
                                    "visibility": "hidden"
                                }
                            }
                        }
                    }
                },
                "items": [
                    {
                        "group": "top",
                        "id": "91b0e304-7767-4d90-92a1-ee5ebb30a61c"
                    },
                    {
                        "group": "right",
                        "id": "6e9b6964-c105-4fe4-81b0-5b129807cdad"
                    },
                    {
                        "group": "bottom",
                        "id": "7d275e27-56c6-4aec-b158-f4c64f5e1454"
                    },
                    {
                        "group": "left",
                        "id": "dc31ca72-2cae-4dfb-9b4e-e2350b3060f6"
                    }
                ]
            },
            "id": "7bb8fbed-83c1-43e8-adf9-86e7125857f2",
            "zIndex": 6
        }
    ]
}